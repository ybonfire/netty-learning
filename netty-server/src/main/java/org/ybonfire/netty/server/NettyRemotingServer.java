package org.ybonfire.netty.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.ybonfire.netty.common.codec.Decoder;
import org.ybonfire.netty.common.codec.Encoder;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.model.Pair;
import org.ybonfire.netty.common.protocol.ResponseCodeConstant;
import org.ybonfire.netty.common.server.IRemotingServer;
import org.ybonfire.netty.common.util.CodecUtil;
import org.ybonfire.netty.server.callback.IResponseCallback;
import org.ybonfire.netty.server.callback.impl.DefaultResponseCallback;
import org.ybonfire.netty.server.config.NettyServerConfig;
import org.ybonfire.netty.server.dispatcher.IRemotingRequestDispatcher;
import org.ybonfire.netty.server.dispatcher.impl.NettyRemotingRequestDispatcher;
import org.ybonfire.netty.server.handler.INettyRemotingRequestHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.ybonfire.netty.server.thread.RequestHandleThreadTask;
import org.ybonfire.netty.server.thread.RequestHandleThreadTaskBuilder;

/**
 * Netty远程调用服务器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:34
 */
public class NettyRemotingServer implements IRemotingServer<ChannelHandlerContext, INettyRemotingRequestHandler> {
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final Encoder encoder = new Encoder();
    private final Decoder decoder = new Decoder();
    private final NettyServerConfig config;
    private final EventLoopGroup parentGroup;
    private final EventLoopGroup childGroup;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;
    private final IRemotingRequestDispatcher<ChannelHandlerContext, INettyRemotingRequestHandler> dispatcher =
        new NettyRemotingRequestDispatcher();
    private final NettyServerHandler nettyServerHandler = new NettyServerHandler();
    private final IResponseCallback callback = new DefaultResponseCallback();

    public NettyRemotingServer(final NettyServerConfig config) {
        this.config = config;

        // parentGroup
        final int parentGroupThreadNums = this.config.getAcceptorThreadNums();
        this.parentGroup = buildEventLoop(parentGroupThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("ParentEventLoop_%d", this.threadIndex.incrementAndGet()));
            }
        });

        // childGroup
        final int childGroupThreadNums = this.config.getAcceptorThreadNums();
        this.childGroup = buildEventLoop(childGroupThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("ChildEventLoop_%d", this.threadIndex.incrementAndGet()));
            }
        });

        // executorGroup
        final int executorThreadNums = this.config.getWorkerThreadNums();
        this.defaultEventExecutorGroup = buildDefaultEventExecutorGroup(executorThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("EventExecutor_%d", this.threadIndex.incrementAndGet()));
            }
        });
    }

    /**
     * @description: 启动服务
     * @param:
     * @return:
     * @date: 2022/05/18 11:51:05
     */
    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            this.serverBootstrap.group(this.parentGroup, this.childGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false).childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, this.config.getServerSocketSendBufferSize())
                .childOption(ChannelOption.SO_RCVBUF, this.config.getServerSocketReceiveBufferSize())
                .localAddress(new InetSocketAddress(this.config.getPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup, encoder, decoder, nettyServerHandler);
                    }
                });

            try {
                ChannelFuture sync = this.serverBootstrap.bind().sync();
                InetSocketAddress addr = (InetSocketAddress)sync.channel().localAddress();
                System.out.println();
            } catch (InterruptedException e1) {
                throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
            }
        }
    }

    /**
     * @description: 关闭服务
     * @param:
     * @return:
     * @date: 2022/05/18 11:51:26
     */
    @Override
    public void shutdown() {
        if (started.compareAndSet(true, false)) {
            // parentGroup
            if (this.parentGroup != null) {
                this.parentGroup.shutdownGracefully();
            }

            // childGroup
            if (this.childGroup != null) {
                this.childGroup.shutdownGracefully();
            }

            // defaultEventExecutorGroup

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
    public void registerRequestHandler(final int requestCode, final INettyRemotingRequestHandler handler,
        final ExecutorService executor) {
        dispatcher.registerRemotingRequestHandler(requestCode, handler, executor);
    }

    /**
     * @description: 处理网络请求
     * @param:
     * @return:
     * @date: 2022/05/18 16:27:19
     */
    private void handleRequestCommand(final ChannelHandlerContext context, final RemotingCommand cmd) {
        final Optional<Pair<INettyRemotingRequestHandler, ExecutorService>> pairOptional =
            this.dispatcher.dispatch(cmd);
        if (pairOptional.isPresent()) {
            final Pair<INettyRemotingRequestHandler, ExecutorService> pair = pairOptional.get();
            final INettyRemotingRequestHandler handler = pair.getKey();
            final ExecutorService executorService = pair.getValue();

            // 构造请求处理异步任务
            final RequestHandleThreadTask task =
                RequestHandleThreadTaskBuilder.build(handler, cmd, context, this.callback);
            executorService.submit(task);
        } else {
            String error = "request type " + cmd.getCode() + " not supported";
            final RemotingCommand response = RemotingCommand.createResponseCommand(
                ResponseCodeConstant.REQUEST_CODE_NOT_SUPPORTED, CodecUtil.toBytes(error), cmd.getRequestId());
            response.setRequestId(cmd.getRequestId());
            this.callback.callback(context, response);
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
     * @description: 请求路由器
     * @author: Bo.Yuan5
     * @date: 2022/5/18
     */
    private class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        /**
         * @description: 处理请求
         * @param:
         * @return:
         * @date: 2022/05/18 18:13:20
         */
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final RemotingCommand msg) throws Exception {
            NettyRemotingServer.this.handleRequestCommand(ctx, msg);
        }
    }
}
