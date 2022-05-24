package org.ybonfire.netty.client;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ybonfire.netty.client.callback.IRequestCallback;
import org.ybonfire.netty.client.config.NettyClientConfig;
import org.ybonfire.netty.client.dispatcher.IRemotingResponseDispatcher;
import org.ybonfire.netty.client.dispatcher.impl.NettyRemotingResponseDispatcher;
import org.ybonfire.netty.client.handler.INettyRemotingResponseHandler;
import org.ybonfire.netty.client.handler.impl.DefaultNettyRemotingResponseHandler;
import org.ybonfire.netty.client.manager.InflightRequestManager;
import org.ybonfire.netty.client.manager.NettyChannelManager;
import org.ybonfire.netty.client.model.RemoteRequestFuture;
import org.ybonfire.netty.client.thread.ClientChannelEventHandleThreadService;
import org.ybonfire.netty.common.client.IRemotingClient;
import org.ybonfire.netty.common.codec.Decoder;
import org.ybonfire.netty.common.codec.Encoder;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.model.NettyChannelEvent;
import org.ybonfire.netty.common.model.NettyChannelEventTypeEnum;
import org.ybonfire.netty.common.model.Pair;
import org.ybonfire.netty.common.protocol.RemotingCommandConstant;
import org.ybonfire.netty.common.protocol.RequestTypeEnum;
import org.ybonfire.netty.common.protocol.ResponseCodeConstant;
import org.ybonfire.netty.common.util.AssertUtils;
import org.ybonfire.netty.common.util.RemotingUtil;
import org.ybonfire.netty.common.util.ThreadPoolUtil;

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
public class NettyRemotingClient implements IRemotingClient<ChannelHandlerContext, INettyRemotingResponseHandler> {
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final Bootstrap bootstrap = new Bootstrap();
    private final Encoder encoder = new Encoder();
    private final NettyClientConfig config;
    private final EventLoopGroup clientEventLoopGroup;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;
    private final NettyChannelManager channelManager = new NettyChannelManager(bootstrap);
    private final ClientChannelEventHandleThreadService channelEventHandleThreadService =
        new ClientChannelEventHandleThreadService(channelManager);
    private final IRemotingResponseDispatcher<ChannelHandlerContext, INettyRemotingResponseHandler> dispatcher =
        new NettyRemotingResponseDispatcher();
    private final InflightRequestManager inflightRequestManager = new InflightRequestManager();
    private final INettyRemotingResponseHandler responseHandler =
        new DefaultNettyRemotingResponseHandler(this.inflightRequestManager);
    private final ExecutorService testExecutorService = ThreadPoolUtil.getTestExecutorService();

    public NettyRemotingClient(final NettyClientConfig config) {
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
            this.registerHandler(ResponseCodeConstant.SUCCESS, responseHandler, this.testExecutorService);

            // start ChannelEventHandleThreadService
            this.channelEventHandleThreadService.start();

            // start client
            this.bootstrap.group(this.clientEventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_SNDBUF, this.config.getClientSocketSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, this.config.getClientSocketReceiveBufferSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup, encoder, new Decoder(),
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
     * @description: 构造EventLoopGroup
     * @param:
     * @return:
     * @date: 2022/05/18 12:21:07
     */
    private EventLoopGroup buildEventLoop(final int theadNums, final ThreadFactory threadFactory) {
        return new NioEventLoopGroup(theadNums, threadFactory);
    }

    /**
     * @description: 同步调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:08
     */
    @Override
    public RemotingCommand request(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException {
        acquireOK();
        return doRequest(address, request, timeoutMillis, RequestTypeEnum.SYNC);
    }

    /**
     * @description: 异步调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:13
     */
    @Override
    public void requestAsync(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException {
        acquireOK();
        doRequest(address, request, timeoutMillis, RequestTypeEnum.ASYNC);
    }

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:18
     */
    @Override
    public void requestOneWay(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException {
        acquireOK();
        doRequest(address, request, timeoutMillis, RequestTypeEnum.ONEWAY);
    }

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/24 00:22:15
     */
    @Override
    public void registerHandler(final int responseCode, final INettyRemotingResponseHandler handler,
        final ExecutorService executor) {
        this.dispatcher.registerRemotingRequestHandler(responseCode, handler, executor);
    }

    /**
     * @description: 构造RemoteRequestFuture
     * @param:
     * @return:
     * @date: 2022/05/19 13:26:46
     */
    private RemoteRequestFuture buildRemoteRequestFuture(final String address, final Channel channel,
        final RemotingCommand request, final long timeoutMillis) {
        return new RemoteRequestFuture(address, channel, request, timeoutMillis);
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
    private RemotingCommand doRequest(final String address, final RemotingCommand request, final long timeoutMillis,
        final RequestTypeEnum type) throws InterruptedException {
        final long startTimestamp = System.currentTimeMillis();

        AssertUtils.notNull(address);
        AssertUtils.notNull(request);
        AssertUtils.notNull(type);

        /** 建立连接 **/
        final Channel channel = this.channelManager.getOrCreateNettyChannel(address, timeoutMillis);
        final long remainingTimeoutMillis = timeoutMillis - (System.currentTimeMillis() - startTimestamp);

        /** 缓存在途请求 **/
        final RemoteRequestFuture future = buildRemoteRequestFuture(address, channel, request, remainingTimeoutMillis);
        this.inflightRequestManager.add(future);

        /** 发送请求 **/
        switch (type) {
            case SYNC:
                final RemotingCommand response = doRequestSync(future, remainingTimeoutMillis);
                return response;
            case ASYNC:
                doRequestAsync(future, remainingTimeoutMillis, null);
                return null;
            case ONEWAY:
                doRequestOneWay(future, remainingTimeoutMillis);
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
    private RemotingCommand doRequestSync(final RemoteRequestFuture future, final long timeoutMillis)
        throws InterruptedException {
        try {
            /** 发送请求 **/
            future.getChannel().writeAndFlush(future.getRequest()).addListener(f -> {
                if (f.isSuccess()) {
                    future.setRequestSuccess(true);
                } else {
                    future.setRequestSuccess(false);
                    future.setCause(f.cause());

                    // 请求发送失败，移除在途的请求
                    this.inflightRequestManager.remove(future.getRequest().getCommandId());
                }
            });

            /** 等待响应 **/
            final RemotingCommand response = future.get(timeoutMillis);
            if (response == null) {
                if (future.isRequestSuccess()) { // 请求成功，但未响应
                    return RemotingCommand.createResponseCommand(ResponseCodeConstant.SERVER_NOT_RESPONSE, "服务未响应",
                        future.getRequest().getCommandId());
                } else { // 请求失败
                    return RemotingCommand.createResponseCommand(ResponseCodeConstant.REQUEST_TIMEOUT, "请求超时",
                        future.getRequest().getCommandId());
                }
            }

            return response;
        } finally {
            this.inflightRequestManager.remove(future.getRequest().getCommandId());
        }
    }

    /**
     * @description: 执行异步调用
     * @param:
     * @return:
     * @date: 2022/05/19 15:05:58
     */
    private void doRequestAsync(final RemoteRequestFuture future, final long timeoutMillis,
        final IRequestCallback callback) {

    }

    /**
     * @description: 执行单向调用
     * @param:
     * @return:
     * @date: 2022/05/19 15:06:45
     */
    private void doRequestOneWay(final RemoteRequestFuture future, final long timeoutMillis) {

    }

    /**
     * @description: 处理响应
     * @param:
     * @return:
     * @date: 2022/05/23 23:58:06
     */
    private void handleResponseCommand(final ChannelHandlerContext context, final RemotingCommand response) {
        final Optional<Pair<INettyRemotingResponseHandler, ExecutorService>> pairOptional =
            this.dispatcher.dispatch(response);
        if (pairOptional.isPresent()) {
            final Pair<INettyRemotingResponseHandler, ExecutorService> pair = pairOptional.get();
            final INettyRemotingResponseHandler handler = pair.getKey();
            final ExecutorService executorService = pair.getValue();

            executorService.submit(() -> handler.handle(response, context));
        } else {
            String error = "response type " + response.getCode() + " not supported";
            System.err.println(error);
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
    private class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        /**
         * @description: 处理响应
         * @param:
         * @return:
         * @date: 2022/05/23 23:56:29
         */
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final RemotingCommand msg) throws Exception {
            if (msg.getCommandType() == RemotingCommandConstant.REMOTING_COMMAND_RESPONSE) { // Response
                NettyRemotingClient.this.handleResponseCommand(ctx, msg);
            } else { // Request
                // TODO
                System.err.println("异常的远程事件类型");
            }
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
            System.out.println("NETTY CLIENT PIPELINE: CONNECT " + local + "->" + remote);

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
            System.out.println("NETTY CLIENT PIPELINE: DISCONNECT " + remote);

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
            System.out.println("NETTY CLIENT PIPELINE: DISCONNECT " + remote);

            // 发布关闭事件
            final NettyChannelEvent event =
                buildNettyChannelEvent(NettyChannelEventTypeEnum.CLOSE, remote, ctx.channel());
            channelEventHandleThreadService.putEvent(event);
        }
    }
}
