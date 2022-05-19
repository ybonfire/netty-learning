package org.ybonfire.netty.server.thread;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.protocol.ResponseCodeConstant;
import org.ybonfire.netty.common.thread.AbstractThreadTask;
import org.ybonfire.netty.common.util.CodecUtil;
import org.ybonfire.netty.server.callback.IResponseCallback;
import org.ybonfire.netty.server.handler.INettyRemotingRequestHandler;

import java.util.UUID;

/**
 * 服务端请求处理异步任务
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:15
 */
@Slf4j
public final class RequestHandleThreadTask extends AbstractThreadTask {
    private final INettyRemotingRequestHandler handler;
    private final RemotingCommand request;
    private final ChannelHandlerContext context;
    private final IResponseCallback callback;

    RequestHandleThreadTask(final INettyRemotingRequestHandler handler, final RemotingCommand request,
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
            final RemotingCommand response = handler.handle(this.request, this.context);
            callback.callback(this.context, response);
        } catch (Throwable ex) {
            final String error = "请求处理失败";
            log.error(error, ex);
            final RemotingCommand response = RemotingCommand.createResponseCommand(
                ResponseCodeConstant.INTERNAL_SYSTEM_ERROR, CodecUtil.toBytes(error), this.request.getRequestId());
            callback.callback(this.context, response);
        }
    }
}
