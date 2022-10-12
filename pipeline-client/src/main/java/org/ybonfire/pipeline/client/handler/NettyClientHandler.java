package org.ybonfire.pipeline.client.handler;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ObjectUtils;
import org.ybonfire.pipeline.client.dispatcher.impl.NettyRemotingResponseDispatcher;
import org.ybonfire.pipeline.client.processor.IRemotingResponseProcessor;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.util.ThreadWorkerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端事件处理器
 *
 * @author yuanbo
 * @date 2022-10-12 17:44
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<RemotingResponse> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final NettyClientHandler INSTANCE = new NettyClientHandler();
    private final ExecutorService defaultExecutor =
        Executors.newFixedThreadPool(4, new ThreadWorkerFactory("client_default_processor_", true));

    private NettyClientHandler() {}

    /**
     * @description: 处理响应
     * @param:
     * @return:
     * @date: 2022/05/23 23:56:29
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final RemotingResponse msg) throws Exception {
        handleResponseCommand(msg);
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

        final Optional<Pair<IRemotingResponseProcessor, ExecutorService>> pairOptional =
            NettyRemotingResponseDispatcher.getInstance().dispatch(response);
        if (pairOptional.isPresent()) {
            final Pair<IRemotingResponseProcessor, ExecutorService> pair = pairOptional.get();
            final IRemotingResponseProcessor processor = pair.getKey();
            final ExecutorService executorService = ObjectUtils.defaultIfNull(pair.getValue(), defaultExecutor);

            executorService.submit(() -> processor.process(response));
        } else {
            String message = "response type " + response.getCode() + " not supported";
            LOGGER.error(message);
        }
    }

    /**
     * 获取NettyClientHandler实例
     *
     * @return {@link NettyClientHandler}
     */
    public static NettyClientHandler getInstance() {
        return INSTANCE;
    }
}
