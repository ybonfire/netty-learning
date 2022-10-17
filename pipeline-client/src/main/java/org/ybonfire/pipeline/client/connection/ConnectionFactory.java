package org.ybonfire.pipeline.client.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.client.exception.ConnectFailedException;
import org.ybonfire.pipeline.client.exception.ConnectTimeoutException;
import org.ybonfire.pipeline.client.handler.NettyClientHandler;
import org.ybonfire.pipeline.client.handler.NettyConnectEventHandler;
import org.ybonfire.pipeline.common.codec.request.RequestEncoder;
import org.ybonfire.pipeline.common.codec.response.ResponseDecoder;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.util.NettyUtil;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 连接工厂
 *
 * @author yuanbo
 * @date 2022-10-17 14:57
 */
public final class ConnectionFactory implements ILifeCycle {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ConnectionFactory INSTANCE = new ConnectionFactory();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup clientEventLoopGroup;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private ConnectionFactory() {}

    /**
     * @description: 启动服务
     * @param:
     * @return:
     * @date: 2022/10/12 10:22:20
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            onStart();
        }
    }

    /**
     * @description: 判断服务是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 10:22:20
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭服务
     * @param:
     * @return:
     * @date: 2022/10/12 10:22:20
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            onShutdown();
        }
    }

    /**
     * @description: 启动流程
     * @param:
     * @return:
     * @date: 2022/10/17 17:22:40
     */
    private void onStart() {
        final NettyClientConfig config = NettyClientConfig.getInstance();

        // eventLoopGroup
        final int eventLoopGroupThreadNums = config.getClientEventLoopThread();
        clientEventLoopGroup = NettyUtil.newEventLoopGroup(eventLoopGroupThreadNums, new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("ClientEventLoopGroup_%d", this.threadIndex.incrementAndGet()));
            }
        });

        // executorGroup
        final int executorThreadNums = config.getWorkerThreadNums();
        defaultEventExecutorGroup = NettyUtil.newDefaultEventExecutorGroup(executorThreadNums, new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("EventExecutor_%d", this.threadIndex.incrementAndGet()));
            }
        });

        // start bootstrap
        bootstrap.group(clientEventLoopGroup).channel(NettyUtil.getClientSocketChannelClass())
            .option(ChannelOption.SO_SNDBUF, config.getClientSocketSendBufferSize())
            .option(ChannelOption.SO_RCVBUF, config.getClientSocketReceiveBufferSize())
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(defaultEventExecutorGroup, new RequestEncoder(), new ResponseDecoder(),
                        NettyConnectEventHandler.getInstance(), NettyClientHandler.getInstance());
                }
            });
    }

    private void onShutdown() {
        // clientEventLoopGroup
        if (this.clientEventLoopGroup != null) {
            this.clientEventLoopGroup.shutdownGracefully();
        }

        // executorGroup
        if (this.defaultEventExecutorGroup != null) {
            this.defaultEventExecutorGroup.shutdownGracefully();
        }
    }

    /**
     * @description: 建立指定地址的连接
     * @param:
     * @return:
     * @date: 2022/10/17 14:59:34
     */
    public Connection create(final String address, final long connectTimeoutMillis) {
        // 确保服务已启动
        acquireOK();

        // 参数校验
        check(address, connectTimeoutMillis);

        final ChannelFuture future = bootstrap.connect(RemotingUtil.addressToSocketAddress(address));
        if (future.awaitUninterruptibly(connectTimeoutMillis, TimeUnit.MILLISECONDS)) {
            final Channel channel = future.channel();
            if (channel.isActive()) {
                return new Connection(channel.id().asShortText(), address, channel);
            } else {
                // connect failed
                LOGGER.error("远程连接失败");
                throw new ConnectFailedException();
            }
        } else {
            // connect timeout
            LOGGER.error("远程连接超时");
            throw new ConnectTimeoutException();
        }
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/10/17 15:03:08
     */
    private void check(final String address, final long connectTimeoutMillis) {
        if (StringUtils.isBlank(address) || connectTimeoutMillis <= 0L) {
            throw new ConnectFailedException(
                String.format("failed to create connection, illegal argument. address: %s, timeout: %d", address,
                    connectTimeoutMillis));
        }
    }

    /**
     * @description: 确保服务已就绪
     * @param:
     * @return:
     * @date: 2022/07/14 14:37:04
     */
    private void acquireOK() {
        if (!this.isStarted.get()) {
            throw new LifeCycleException();
        }
    }

    /**
     * 获取ConnectionFactory实例
     *
     * @return {@link ConnectionFactory}
     */
    public static ConnectionFactory getInstance() {
        return INSTANCE;
    }
}
