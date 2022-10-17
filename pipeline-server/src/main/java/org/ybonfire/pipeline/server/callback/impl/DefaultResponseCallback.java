package org.ybonfire.pipeline.server.callback.impl;

import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.server.callback.IResponseCallback;
import org.ybonfire.pipeline.server.exception.ServerException;
import org.ybonfire.pipeline.server.exception.handler.ServerExceptionHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * 默认请求处理回调
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:41
 */
public class DefaultResponseCallback implements IResponseCallback {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();

    /**
     * @description: 成功响应回调流程
     * @param:
     * @return:
     * @date: 2022/05/18 18:07:46
     */
    @Override
    public void onSuccess(final IRemotingResponse response, final ChannelHandlerContext context) {
        context.writeAndFlush(response).addListener(f -> {
            if (!f.isSuccess()) {
                LOGGER.error(String.format("Failed to send response. id:[%s]", response.getId()));
            }
        });
    }

    /**
     * @description: 异常响应回调流程
     * @param:
     * @return:
     * @date: 2022/09/09 14:31:01
     */
    @Override
    public void onException(final IRemotingRequest request, final Exception ex, final ChannelHandlerContext context) {
        final ServerExceptionHandler exceptionHandler = ServerExceptionHandler.getInstance();
        final RemotingResponse<?> response = ex instanceof ServerException
            ? exceptionHandler.handle(request, (ServerException)ex) : exceptionHandler.handle(request, ex);
        context.writeAndFlush(response).addListener(f -> {
            if (!f.isSuccess()) {
                LOGGER.error(String.format("Failed to send response. id:[%s]", response.getId()));
            }
        });
    }
}
