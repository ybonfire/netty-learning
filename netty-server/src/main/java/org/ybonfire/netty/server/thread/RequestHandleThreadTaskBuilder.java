package org.ybonfire.netty.server.thread;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.server.callback.IResponseCallback;
import org.ybonfire.netty.server.handler.INettyRemotingRequestHandler;

/**
 * 服务端请求处理异步任务构造器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestHandleThreadTaskBuilder {

    /**
     * @description: 构造服务端请求处理异步任务
     * @param:
     * @return:
     * @date: 2022/05/18 17:30:30
     */
    public static RequestHandleThreadTask build(final INettyRemotingRequestHandler handler,
        final RemotingCommand request, final ChannelHandlerContext context, final IResponseCallback callback) {
        return new RequestHandleThreadTask(handler, request, context, callback);
    }
}
