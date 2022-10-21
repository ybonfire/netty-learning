package org.ybonfire.pipeline.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.ybonfire.pipeline.client.processor.impl.DefaultRemotingResponseProcessor;
import org.ybonfire.pipeline.client.util.ThreadPoolUtil;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

import java.util.concurrent.ExecutorService;

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

        final ExecutorService executorService = ThreadPoolUtil.getResponseProcessorExecutorService();
        executorService.submit(() -> DefaultRemotingResponseProcessor.getInstance().process(response));
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
