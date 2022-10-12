package org.ybonfire.pipeline.server.thread;

import java.util.UUID;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.thread.task.AbstractThreadTask;
import org.ybonfire.pipeline.server.callback.IResponseCallback;
import org.ybonfire.pipeline.server.exception.ServerException;
import org.ybonfire.pipeline.server.exception.UnknownException;
import org.ybonfire.pipeline.server.processor.IRemotingRequestProcessor;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端请求处理异步任务
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:15
 */
@Slf4j
public final class RequestProcessThreadTask extends AbstractThreadTask {
    private final IRemotingRequestProcessor processor;
    private final IRemotingRequest request;
    private final ChannelHandlerContext context;
    private final IResponseCallback callback;

    RequestProcessThreadTask(final IRemotingRequestProcessor processor, final IRemotingRequest request,
        final ChannelHandlerContext context, final IResponseCallback callback) {
        super(UUID.randomUUID().toString(), (task, ex) -> log.error("异步任务执行失败."));
        this.processor = processor;
        this.request = request;
        this.context = context;
        this.callback = callback;
    }

    /**
     * @description: 服务端请求处理
     * @param:
     * @return:
     * @date: 2022/05/18 17:15:46
     */
    @Override
    protected void execute() {
        try {
            final RemotingResponse<?> response = processor.process(this.request);
            callback.onSuccess(response, this.context);
        } catch (Exception ex) {
            final String error = "请求处理异常";
            log.error(error, ex);

            if (ex instanceof ServerException) {
                callback.onException(request, ex, context);
            } else {
                final ServerException exWrapper = new UnknownException(ex);
                callback.onException(request, exWrapper, context);
            }
        }
    }
}
