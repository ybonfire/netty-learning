package org.ybonfire.pipeline.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.ybonfire.pipeline.common.codec.request.RequestDecoder;
import org.ybonfire.pipeline.common.codec.response.ResponseEncoder;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.util.NettyUtil;
import org.ybonfire.pipeline.server.config.NettyServerConfig;
import org.ybonfire.pipeline.server.dispatcher.impl.NettyRemotingRequestDispatcher;
import org.ybonfire.pipeline.server.exception.StartupException;
import org.ybonfire.pipeline.server.handler.NettyServerHandler;
import org.ybonfire.pipeline.server.processor.IRemotingRequestProcessor;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty远程调用服务器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:34
 */
public abstract class NettyRemotingServer implements IRemotingServer<IRemotingRequestProcessor> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final NettyServerConfig config;
    private final EventLoopGroup parentGroup;
    private final EventLoopGroup childGroup;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    protected NettyRemotingServer(final NettyServerConfig config) {
        this.config = config;

        // parentGroup
        final int parentGroupThreadNums = this.config.getAcceptorThreadNums();
        this.parentGroup = buildEventLoop(parentGroupThreadNums, new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("ParentEventLoop_%d", this.threadIndex.incrementAndGet()));
            }
        });

        // childGroup
        final int childGroupThreadNums = this.config.getAcceptorThreadNums();
        this.childGroup = buildEventLoop(childGroupThreadNums, new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("ChildEventLoop_%d", this.threadIndex.incrementAndGet()));
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
     * @description: 启动服务端
     * @param:
     * @return:
     * @date: 2022/05/18 11:51:05
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            // register processor
            registerRequestProcessor();

            // start server
            this.serverBootstrap.group(this.parentGroup, this.childGroup)
                .channel(NettyUtil.getServerSocketChannelClass()).option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, this.config.getServerSocketSendBufferSize())
                .childOption(ChannelOption.SO_RCVBUF, this.config.getServerSocketReceiveBufferSize())
                .localAddress(new InetSocketAddress(this.config.getPort())).handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        LOGGER.info(ch.remoteAddress().toString());
                        ch.pipeline().addLast(defaultEventExecutorGroup, new ResponseEncoder(), new RequestDecoder(),
                            NettyServerHandler.getInstance());
                    }
                });

            try {
                ChannelFuture future = this.serverBootstrap.bind(this.config.getPort()).sync();
                LOGGER.info(future.channel().localAddress().toString());
                // future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new StartupException("this.serverBootstrap.bind().sync() InterruptedException", e);
            }
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
     * @description: 关闭服务端
     * @param:
     * @return:
     * @date: 2022/05/18 11:51:26
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            // parentGroup
            if (this.parentGroup != null) {
                this.parentGroup.shutdownGracefully();
            }

            // childGroup
            if (this.childGroup != null) {
                this.childGroup.shutdownGracefully();
            }

            // executorGroup
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        }
    }

    /**
     * @description: 注册请求处理器
     * @param:
     * @return:
     * @date: 2022/05/18 10:47:12
     */
    @Override
    public void registerRequestProcessor(final int requestCode, final IRemotingRequestProcessor processor,
        final ExecutorService executor) {
        NettyRemotingRequestDispatcher.getInstance().registerRemotingRequestProcessor(requestCode, processor, executor);
    }

    /**
     * @description: 注册远程调用请求处理器
     * @param:
     * @return:
     * @date: 2022/07/01 17:41:06
     */
    protected abstract void registerRequestProcessor();

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
