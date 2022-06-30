package org.ybonfire.pipeline.server;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ybonfire.pipeline.common.codec.Decoder;
import org.ybonfire.pipeline.common.codec.Encoder;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.constant.RemotingCommandTypeConstant;
import org.ybonfire.pipeline.common.constant.RequestCodeConstant;
import org.ybonfire.pipeline.common.constant.ResponseCodeConstant;
import org.ybonfire.pipeline.common.server.IRemotingServer;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;
import org.ybonfire.pipeline.server.callback.IResponseCallback;
import org.ybonfire.pipeline.server.callback.impl.DefaultResponseCallback;
import org.ybonfire.pipeline.server.config.NettyServerConfig;
import org.ybonfire.pipeline.server.dispatcher.IRemotingRequestDispatcher;
import org.ybonfire.pipeline.server.dispatcher.impl.NettyRemotingRequestDispatcher;
import org.ybonfire.pipeline.server.handler.INettyRemotingRequestHandler;
import org.ybonfire.pipeline.server.handler.impl.DefaultNettyRemotingRequestHandler;
import org.ybonfire.pipeline.server.thread.RequestHandleThreadTask;
import org.ybonfire.pipeline.server.thread.RequestHandleThreadTaskBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Netty远程调用服务器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:34
 */
public class NettyRemotingServer implements IRemotingServer<ChannelHandlerContext, INettyRemotingRequestHandler> {
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final NettyServerConfig config;
    private final EventLoopGroup parentGroup;
    private final EventLoopGroup childGroup;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;
    private final IRemotingRequestDispatcher<ChannelHandlerContext, INettyRemotingRequestHandler> dispatcher =
        new NettyRemotingRequestDispatcher();
    private final NettyServerHandler nettyServerHandler = new NettyServerHandler();
    private final IResponseCallback callback = new DefaultResponseCallback();
    private final ExecutorService testExecutorService = ThreadPoolUtil.getTestExecutorService();

    public NettyRemotingServer(final NettyServerConfig config) {
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
        if (started.compareAndSet(false, true)) {
            // register handler
            this.registerHandler(RequestCodeConstant.TEST_REQUEST_CODE, new DefaultNettyRemotingRequestHandler(),
                this.testExecutorService);

            // start server
            this.serverBootstrap.group(this.parentGroup, this.childGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, this.config.getServerSocketSendBufferSize())
                .childOption(ChannelOption.SO_RCVBUF, this.config.getServerSocketReceiveBufferSize())
                .localAddress(new InetSocketAddress(this.config.getPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println(ch.remoteAddress());
                        ch.pipeline().addLast(defaultEventExecutorGroup, new Encoder(), new Decoder(),
                            nettyServerHandler);
                    }
                });

            try {
                ChannelFuture future = this.serverBootstrap.bind().sync();
                System.out.println(future.channel().localAddress());
                future.channel().closeFuture().sync();
            } catch (InterruptedException e1) {
                throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
            }
        }
    }

    /**
     * @description: 关闭服务端
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
    public void registerHandler(final int requestCode, final INettyRemotingRequestHandler handler,
        final ExecutorService executor) {
        dispatcher.registerRemotingRequestHandler(requestCode, handler, executor);
    }

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/05/18 16:27:19
     */
    private void handleRequestCommand(final ChannelHandlerContext context, final RemotingCommand request) {
        final Optional<Pair<INettyRemotingRequestHandler, ExecutorService>> pairOptional =
            this.dispatcher.dispatch(request);
        if (pairOptional.isPresent()) {
            final Pair<INettyRemotingRequestHandler, ExecutorService> pair = pairOptional.get();
            final INettyRemotingRequestHandler handler = pair.getKey();
            final ExecutorService executorService = pair.getValue();

            // 构造请求处理异步任务
            final RequestHandleThreadTask task =
                RequestHandleThreadTaskBuilder.build(handler, request, context, this.callback);
            executorService.submit(task);
        } else {
            String error = "request type " + request.getCode() + " not supported";
            final RemotingCommand response = RemotingCommand
                .createResponseCommand(ResponseCodeConstant.REQUEST_CODE_NOT_SUPPORTED, request.getCommandId(), error);
            response.setCommandId(request.getCommandId());
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
     * @description: 服务端事件处理器
     * @author: Bo.Yuan5
     * @date: 2022/5/18
     */
    @ChannelHandler.Sharable
    private class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        /**
         * @description: 处理请求
         * @param:
         * @return:
         * @date: 2022/05/18 18:13:20
         */
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final RemotingCommand msg) throws Exception {
            if (msg.getCommandType() == RemotingCommandTypeConstant.REMOTING_COMMAND_REQUEST) { // Request
                NettyRemotingServer.this.handleRequestCommand(ctx, msg);
            } else { // Response
                // TODO
                System.err.println("异常的远程事件类型");
            }
        }
    }
}
