package org.ybonfire.pipeline.client;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ObjectUtils;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.client.dispatcher.IRemotingResponseDispatcher;
import org.ybonfire.pipeline.client.dispatcher.impl.NettyRemotingResponseDispatcher;
import org.ybonfire.pipeline.client.exception.InvokeExecuteException;
import org.ybonfire.pipeline.client.exception.InvokeInterruptedException;
import org.ybonfire.pipeline.client.exception.ReadTimeoutException;
import org.ybonfire.pipeline.client.handler.IRemotingResponseHandler;
import org.ybonfire.pipeline.client.manager.InflightRequestManager;
import org.ybonfire.pipeline.client.manager.NettyChannelManager;
import org.ybonfire.pipeline.client.model.RemoteRequestFuture;
import org.ybonfire.pipeline.client.model.RequestTypeEnum;
import org.ybonfire.pipeline.client.thread.ClientChannelEventHandleThreadService;
import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.codec.request.RequestEncoder;
import org.ybonfire.pipeline.common.codec.response.ResponseDecoder;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.NettyChannelEvent;
import org.ybonfire.pipeline.common.model.NettyChannelEventTypeEnum;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.util.AssertUtils;
import org.ybonfire.pipeline.common.util.RemotingUtil;
import org.ybonfire.pipeline.common.util.ThreadWorkerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Netty远程调用客户端
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 15:28
 */
public abstract class NettyRemotingClient implements IRemotingClient<IRemotingResponseHandler> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final Bootstrap bootstrap = new Bootstrap();
    private final NettyChannelManager channelManager = new NettyChannelManager(bootstrap);
    private final ClientChannelEventHandleThreadService channelEventHandleThreadService =
        new ClientChannelEventHandleThreadService(channelManager);
    private final IRemotingResponseDispatcher<IRemotingResponseHandler> dispatcher =
        new NettyRemotingResponseDispatcher();
    private final InflightRequestManager inflightRequestManager = new InflightRequestManager();
    private final NettyClientConfig config;
    private final EventLoopGroup clientEventLoopGroup;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;
    private final ExecutorService defaultHandler;

    protected NettyRemotingClient(final NettyClientConfig config) {
        this.config = config;

        // eventLoopGroup
        final int eventLoopGroupThreadNums = this.config.getClientEventLoopThread();
        this.clientEventLoopGroup = buildEventLoop(eventLoopGroupThreadNums, new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("ClientEventLoopGroup_%d", this.threadIndex.incrementAndGet()));
            }
        });

        // executorGroup
        final int executorThreadNums = this.config.getWorkerThreadNums();
        this.defaultEventExecutorGroup = buildDefaultEventExecutorGroup(executorThreadNums, new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("EventExecutor_%d", this.threadIndex.incrementAndGet()));
            }
        });

        // defaultHandler
        this.defaultHandler = Executors.newFixedThreadPool(4, new ThreadWorkerFactory("client_default_", true));
    }

    /**
     * @description: 启动客户端
     * @param:
     * @return:
     * @date: 2022/05/18 15:32:53
     */
    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            // register handler
            registerResponseHandlers();

            // start ChannelEventHandleThreadService
            this.channelEventHandleThreadService.start();

            // start client
            this.bootstrap.group(this.clientEventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_SNDBUF, this.config.getClientSocketSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, this.config.getClientSocketReceiveBufferSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup, new RequestEncoder(), new ResponseDecoder(),
                            new NettyConnectEventHandler(), new NettyClientHandler());
                    }
                });
        }
    }

    /**
     * @description: 关闭客户端
     * @param:
     * @return:
     * @date: 2022/05/18 15:32:56
     */
    @Override
    public void shutdown() {
        if (started.compareAndSet(true, false)) {
            // disconnect
            this.channelManager.closeAllChannel();

            // stop ChannelEventHandler
            this.channelEventHandleThreadService.stop();

            // clientEventLoopGroup
            if (this.clientEventLoopGroup != null) {
                this.clientEventLoopGroup.shutdownGracefully();
            }

            // executorGroup
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        }
    }

    /**
     * @description: 同步调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:08
     */
    @Override
    public IRemotingResponse request(final String address, final IRemotingRequest request) {
        acquireOK();
        try {
            return doRequest(address, request, null, RequestTypeEnum.SYNC);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new InvokeInterruptedException(ex);
        }
    }

    /**
     * @description: 异步调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:13
     */
    @Override
    public void requestAsync(final String address, final IRemotingRequest request, final IRequestCallback callback)
        throws InterruptedException {
        acquireOK();
        doRequest(address, request, callback, RequestTypeEnum.ASYNC);
    }

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:18
     */
    @Override
    public void requestOneWay(final String address, final IRemotingRequest request) throws InterruptedException {
        acquireOK();
        doRequest(address, request, null, RequestTypeEnum.ONEWAY);
    }

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/24 00:22:15
     */
    @Override
    public void registerHandler(final int responseCode, final IRemotingResponseHandler handler,
        final ExecutorService executor) {
        this.dispatcher.registerRemotingRequestHandler(responseCode, handler, executor);
    }

    protected InflightRequestManager getInflightRequestManager() {
        return inflightRequestManager;
    }

    /**
     * @description: 注册远程调用响应处理器
     * @param:
     * @return:
     * @date: 2022/07/01 17:41:06
     */
    protected abstract void registerResponseHandlers();

    /**
     * @description: 构造EventLoopGroup
     * @param:
     * @return:
     * @date: 2022/05/18 12:21:07
     */
    private EventLoopGroup buildEventLoop(final int theadNums, final ThreadFactory threadFactory) {
        return new NioEventLoopGroup(theadNums, threadFactory);
    }

    /**
     * @description: 构造RemoteRequestFuture
     * @param:
     * @return:
     * @date: 2022/05/19 13:26:46
     */
    private RemoteRequestFuture buildRemoteRequestFuture(final String address, final Channel channel,
        final IRemotingRequest request, final IRequestCallback callback, final long timeoutMillis) {
        return new RemoteRequestFuture(address, channel, request, callback, timeoutMillis);
    }

    /**
     * @description: 判断客户端是否启动
     * @param:
     * @return:
     * @date: 2022/05/19 11:49:04
     */
    private void acquireOK() {
        if (!this.started.get()) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @description: 执行调用请求
     * @param:
     * @return:
     * @date: 2022/05/19 14:10:22
     */
    private IRemotingResponse doRequest(final String address, final IRemotingRequest request,
        final IRequestCallback callback, final RequestTypeEnum type) throws InterruptedException {
        final long startTimestamp = System.currentTimeMillis();

        // 参数校验
        AssertUtils.notNull(address);
        AssertUtils.notNull(request);
        AssertUtils.notNull(type);

        // 建立连接
        final long connectTimeoutMillis = config.getConnectTimeoutMillis();
        final Channel channel = this.channelManager.getOrCreateNettyChannel(address, connectTimeoutMillis);

        // 缓存在途请求
        final long remainingTimeoutMillis =
            config.getReadTimeoutMillis() - (System.currentTimeMillis() - startTimestamp);
        final RemoteRequestFuture future =
            buildRemoteRequestFuture(address, channel, request, callback, remainingTimeoutMillis);
        this.inflightRequestManager.add(future);

        // 发送请求
        switch (type) {
            case SYNC:
                final IRemotingResponse response = doRequestSync(future, remainingTimeoutMillis);
                return response;
            case ASYNC:
                doRequestAsync(future);
                return null;
            case ONEWAY:
                doRequestOneWay(future);
                return null;
            default:
                // TODO
                throw new UnsupportedOperationException();
        }
    }

    /**
     * @description: 执行同步请求
     * @param:
     * @return:
     * @date: 2022/05/19 15:03:49
     */
    private IRemotingResponse doRequestSync(final RemoteRequestFuture future, final long timeoutMillis)
        throws InterruptedException {
        try {
            // 发送请求
            future.getChannel().writeAndFlush(future.getRequest()).addListener(f -> {
                if (f.isSuccess()) {
                    future.setRequestSuccess(true);
                } else {
                    // TODO log
                    future.setRequestSuccess(false);
                    future.setCause(f.cause());
                }
            });

            // 请求失败
            if (!future.isRequestSuccess()) {
                throw new InvokeExecuteException(future.getCause());
            }

            // 请求成功, 等待响应
            final IRemotingResponse response = future.get(timeoutMillis);
            if (response == null) {
                throw new ReadTimeoutException();
            }

            return response;
        } finally {
            // 移除在途请求
            this.inflightRequestManager.remove(future.getRequest().getId());
        }
    }

    /**
     * @description: 执行异步调用
     * @param:
     * @return:
     * @date: 2022/05/19 15:05:58
     */
    private void doRequestAsync(final RemoteRequestFuture future) {
        try {
            // 发送请求
            future.getChannel().writeAndFlush(future.getRequest()).addListener(f -> {
                if (f.isSuccess()) {
                    future.setRequestSuccess(true);
                } else {
                    // TODO log
                    future.setRequestSuccess(false);
                    future.setCause(f.cause());
                }
            });

            // 请求失败
            if (!future.isRequestSuccess()) {
                throw new InvokeExecuteException(future.getCause());
            }
        } finally {
            // 移除在途请求
            if (!future.isRequestSuccess()) {
                this.inflightRequestManager.remove(future.getRequest().getId());
            }
        }
    }

    /**
     * @description: 执行单向调用
     * @param:
     * @return:
     * @date: 2022/05/19 15:06:45
     */
    private void doRequestOneWay(final RemoteRequestFuture future) {
        try {
            // 发送请求
            future.getChannel().writeAndFlush(future.getRequest());
        } finally {
            // 移除在途请求
            this.inflightRequestManager.remove(future.getRequest().getId());
        }
    }

    /**
     * @description: 处理响应
     * @param:
     * @return:
     * @date: 2022/05/23 23:58:06
     */
    private void handleResponseCommand(final RemotingResponse response) {
        if (response == null) {
            return;
        }

        final Optional<Pair<IRemotingResponseHandler, ExecutorService>> pairOptional =
            this.dispatcher.dispatch(response);
        if (pairOptional.isPresent()) {
            final Pair<IRemotingResponseHandler, ExecutorService> pair = pairOptional.get();
            final IRemotingResponseHandler handler = pair.getKey();
            final ExecutorService executorService = ObjectUtils.defaultIfNull(pair.getValue(), defaultHandler);

            executorService.submit(() -> handler.handle(response));
        } else {
            String message = "response type " + response.getCode() + " not supported";
            LOGGER.error(message);
        }
    }

    /**
     * @description: 构造DefaultEventExecutorGroup
     * @param:
     * @return:
     * @date: 2022/05/18 14:48:57
     */
    private DefaultEventExecutorGroup buildDefaultEventExecutorGroup(final int threadNums,
        final ThreadFactory threadFactory) {
        return new DefaultEventExecutorGroup(threadNums, threadFactory);
    }

    /**
     * @description: 构造NettyChannelEvent
     * @param:
     * @return:
     * @date: 2022/05/24 23:06:17
     */
    private NettyChannelEvent buildNettyChannelEvent(final NettyChannelEventTypeEnum type, final String address,
        final Channel channel) {
        return new NettyChannelEvent(type, address, channel);
    }

    /**
     * @description: 客户端事件处理器
     * @author: Bo.Yuan5
     * @date: 2022/5/23
     */
    @ChannelHandler.Sharable
    private class NettyClientHandler extends SimpleChannelInboundHandler<RemotingResponse> {

        /**
         * @description: 处理响应
         * @param:
         * @return:
         * @date: 2022/05/23 23:56:29
         */
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final RemotingResponse msg) throws Exception {
            NettyRemotingClient.this.handleResponseCommand(msg);
        }
    }

    /**
     * @description: Netty连接事件处理器
     * @author: Bo.Yuan5
     * @date: 2022/5/24
     */
    private class NettyConnectEventHandler extends ChannelDuplexHandler {

        /**
         * @description: 建立连接
         * @param:
         * @return:
         * @date: 2022/05/24 23:01:03
         */
        @Override
        public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress,
            final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
            // 建立连接
            super.connect(ctx, remoteAddress, localAddress, promise);

            final String local = localAddress == null ? "UNKNOWN" : localAddress.toString();
            final String remote = remoteAddress == null ? "UNKNOWN" : remoteAddress.toString();
            LOGGER.info("NETTY CLIENT PIPELINE: CONNECT " + local + "->" + remote);

            // 发布连接事件
            final NettyChannelEvent event =
                buildNettyChannelEvent(NettyChannelEventTypeEnum.OPEN, remote, ctx.channel());
            channelEventHandleThreadService.putEvent(event);
        }

        /**
         * @description: 断开连接
         * @param:
         * @return:
         * @date: 2022/05/24 23:01:20
         */
        @Override
        public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
            // 断开连接
            super.disconnect(ctx, promise);

            final String remote = RemotingUtil.parseChannelAddress(ctx.channel());
            LOGGER.info("NETTY CLIENT PIPELINE: DISCONNECT " + remote);

            // 发布关闭事件
            final NettyChannelEvent event =
                buildNettyChannelEvent(NettyChannelEventTypeEnum.CLOSE, remote, ctx.channel());
            channelEventHandleThreadService.putEvent(event);
        }

        /**
         * @description: 关闭连接
         * @param:
         * @return:
         * @date: 2022/05/24 23:01:35
         */
        @Override
        public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
            // 断开连接
            super.disconnect(ctx, promise);

            final String remote = RemotingUtil.parseChannelAddress(ctx.channel());
            LOGGER.info("NETTY CLIENT PIPELINE: DISCONNECT " + remote);

            // 发布关闭事件
            final NettyChannelEvent event =
                buildNettyChannelEvent(NettyChannelEventTypeEnum.CLOSE, remote, ctx.channel());
            channelEventHandleThreadService.putEvent(event);
        }
    }
}
