package org.ybonfire.pipeline.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.client.dispatcher.impl.NettyRemotingResponseDispatcher;
import org.ybonfire.pipeline.client.exception.InvokeExecuteException;
import org.ybonfire.pipeline.client.exception.InvokeInterruptedException;
import org.ybonfire.pipeline.client.exception.ReadTimeoutException;
import org.ybonfire.pipeline.client.exception.UnSupportedRequestTypeException;
import org.ybonfire.pipeline.client.handler.NettyClientHandler;
import org.ybonfire.pipeline.client.handler.NettyConnectEventHandler;
import org.ybonfire.pipeline.client.manager.InflightRequestManager;
import org.ybonfire.pipeline.client.manager.NettyChannelManager;
import org.ybonfire.pipeline.client.model.RemoteRequestFuture;
import org.ybonfire.pipeline.client.model.RequestTypeEnum;
import org.ybonfire.pipeline.client.processor.IRemotingResponseProcessor;
import org.ybonfire.pipeline.client.thread.ClientChannelEventHandleThreadService;
import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.codec.request.RequestEncoder;
import org.ybonfire.pipeline.common.codec.response.ResponseDecoder;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.util.AssertUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty远程调用客户端
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 15:28
 */
public abstract class NettyRemotingClient implements IRemotingClient<IRemotingResponseProcessor> {
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final Bootstrap bootstrap = new Bootstrap();
    private final NettyChannelManager channelManager = new NettyChannelManager(bootstrap);
    private final ClientChannelEventHandleThreadService channelEventHandleThreadService =
        new ClientChannelEventHandleThreadService(channelManager);
    private final InflightRequestManager inflightRequestManager = InflightRequestManager.getInstance();
    private final NettyClientConfig config;
    private final EventLoopGroup clientEventLoopGroup;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

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
    }

    /**
     * @description: 启动客户端
     * @param:
     * @return:
     * @date: 2022/05/18 15:32:53
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            // register processor
            registerResponseProcessors();

            // start ChannelEventHandleThreadService
            channelEventHandleThreadService.start();

            // start client
            bootstrap.group(this.clientEventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_SNDBUF, this.config.getClientSocketSendBufferSize())
                .option(ChannelOption.SO_RCVBUF, this.config.getClientSocketReceiveBufferSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup, new RequestEncoder(), new ResponseDecoder(),
                            new NettyConnectEventHandler(), NettyClientHandler.getInstance());
                    }
                });

            // start inflight requests manager
            inflightRequestManager.start();
        }
    }

    /**
     * @description: 判断是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:37
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭客户端
     * @param:
     * @return:
     * @date: 2022/05/18 15:32:56
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
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
    public IRemotingResponse request(final String address, final IRemotingRequest request, final long timeoutMillis) {
        acquireOK();
        try {
            return doRequest(address, request, null, timeoutMillis, RequestTypeEnum.SYNC);
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
    public void requestAsync(final String address, final IRemotingRequest request, final IRequestCallback callback,
        final long timeoutMillis) throws InterruptedException {
        acquireOK();
        doRequest(address, request, callback, timeoutMillis, RequestTypeEnum.ASYNC);
    }

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:18
     */
    @Override
    public void requestOneway(final String address, final IRemotingRequest request) throws InterruptedException {
        acquireOK();
        doRequest(address, request, null, -1L, RequestTypeEnum.ONEWAY);
    }

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/24 00:22:15
     */
    @Override
    public void registerResponseProcessor(final int responseCode, final IRemotingResponseProcessor processor,
        final ExecutorService executor) {
        NettyRemotingResponseDispatcher.getInstance().registerRemotingRequestProcessor(responseCode, processor,
            executor);
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
    protected abstract void registerResponseProcessors();

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
     * @description: 确保服务已就绪
     * @param:
     * @return:
     * @date: 2022/05/19 11:49:04
     */
    private void acquireOK() {
        if (!this.isStarted.get()) {
            throw new LifeCycleException();
        }
    }

    /**
     * @description: 执行调用请求
     * @param:
     * @return:
     * @date: 2022/05/19 14:10:22
     */
    private IRemotingResponse doRequest(final String address, final IRemotingRequest request,
        final IRequestCallback callback, final long timeoutMillis, final RequestTypeEnum type)
        throws InterruptedException {
        final long startTimestamp = System.currentTimeMillis();

        // 参数校验
        AssertUtils.notNull(address);
        AssertUtils.notNull(request);
        AssertUtils.notNull(type);

        // 建立连接
        final Channel channel = channelManager.getOrCreateNettyChannel(address, timeoutMillis);
        final long remainingTimeoutMillis =
            config.getReadTimeoutMillis() - (System.currentTimeMillis() - startTimestamp);

        // 缓存在途请求
        final RemoteRequestFuture future =
            buildRemoteRequestFuture(address, channel, request, callback, remainingTimeoutMillis);
        inflightRequestManager.add(future);

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
                throw new UnSupportedRequestTypeException();
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
            inflightRequestManager.remove(future.getRequest().getId());
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
                inflightRequestManager.remove(future.getRequest().getId());
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
            inflightRequestManager.remove(future.getRequest().getId());
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
}
