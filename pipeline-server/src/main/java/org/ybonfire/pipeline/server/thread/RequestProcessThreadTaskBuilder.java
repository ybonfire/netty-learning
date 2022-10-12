package org.ybonfire.pipeline.server.thread;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.server.callback.IResponseCallback;
import org.ybonfire.pipeline.server.processor.IRemotingRequestProcessor;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 服务端请求处理异步任务构造器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestProcessThreadTaskBuilder {

    /**
     * @description: 构造服务端请求处理异步任务
     * @param:
     * @return:
     * @date: 2022/05/18 17:30:30
     */
    public static RequestProcessThreadTask build(final IRemotingRequestProcessor processor,
        final IRemotingRequest request, final ChannelHandlerContext context, final IResponseCallback callback) {
        return new RequestProcessThreadTask(processor, request, context, callback);
    }
}
