package org.ybonfire.pipeline.server.thread;

import java.util.UUID;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.common.thread.task.AbstractThreadTask;
import org.ybonfire.pipeline.server.callback.IResponseCallback;
import org.ybonfire.pipeline.server.handler.IRemotingRequestHandler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端请求处理异步任务
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:15
 */
@Slf4j
public final class RequestHandleThreadTask extends AbstractThreadTask {
    private final IRemotingRequestHandler handler;
    private final IRemotingRequest request;
    private final ChannelHandlerContext context;
    private final IResponseCallback callback;

    RequestHandleThreadTask(final IRemotingRequestHandler handler, final IRemotingRequest request,
        final ChannelHandlerContext context, final IResponseCallback callback) {
        super(UUID.randomUUID().toString(), (task, ex) -> log.error("异步任务执行失败."));
        this.handler = handler;
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
            final IRemotingResponse response = handler.handle(this.request);
            callback.callback(response, this.context);
        } catch (Throwable ex) {
            final String error = "请求处理失败";
            log.error(error, ex);
            final IRemotingResponse response = RemotingResponse.create(request.getId(), request.getCode(),
                ResponseEnum.INTERNAL_SYSTEM_ERROR.getCode(), DefaultResponse.create(error));
            callback.callback(response, this.context);
        }
    }
}
