package org.ybonfire.pipeline.server.handler;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ObjectUtils;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.util.ThreadWorkerFactory;
import org.ybonfire.pipeline.server.callback.IResponseCallback;
import org.ybonfire.pipeline.server.callback.impl.DefaultResponseCallback;
import org.ybonfire.pipeline.server.dispatcher.impl.NettyRemotingRequestDispatcher;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.exception.ServerException;
import org.ybonfire.pipeline.server.processor.IRemotingRequestProcessor;
import org.ybonfire.pipeline.server.thread.RequestProcessThreadTask;
import org.ybonfire.pipeline.server.thread.RequestProcessThreadTaskBuilder;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务端事件处理器
 *
 * @author yuanbo
 * @date 2022-10-12 17:38
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<IRemotingRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final NettyServerHandler INSTANCE = new NettyServerHandler();
    private final IResponseCallback callback = new DefaultResponseCallback();
    private final ExecutorService defaultExecutor =
        Executors.newFixedThreadPool(4, new ThreadWorkerFactory("server_default_processor", true));

    private NettyServerHandler() {}

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/05/18 18:13:20
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final IRemotingRequest msg) {
        handleRequestCommand(ctx, msg);
    }

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/05/18 16:27:19
     */
    private void handleRequestCommand(final ChannelHandlerContext context, final IRemotingRequest request) {
        if (request == null) {
            return;
        }

        final Optional<Pair<IRemotingRequestProcessor, ExecutorService>> pairOptional =
            NettyRemotingRequestDispatcher.getInstance().dispatch(request);
        if (pairOptional.isPresent()) {
            final Pair<IRemotingRequestProcessor, ExecutorService> pair = pairOptional.get();
            final IRemotingRequestProcessor processor = pair.getKey();
            final ExecutorService executorService = ObjectUtils.defaultIfNull(pair.getValue(), defaultExecutor);

            // 构造请求处理异步任务
            final RequestProcessThreadTask task =
                RequestProcessThreadTaskBuilder.build(processor, request, context, this.callback);
            executorService.submit(task);
        } else {
            String message = "request type " + request.getCode() + " not supported";
            LOGGER.warn(message);
            final ServerException ex = new RequestTypeNotSupportException(message);
            this.callback.onException(request, ex, context);
        }
    }

    /**
     * 获取NettyServerHandler实例
     *
     * @return {@link NettyServerHandler}
     */
    public static NettyServerHandler getInstance() {
        return INSTANCE;
    }
}
