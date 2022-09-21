package org.ybonfire.pipeline.server.callback;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;

import io.netty.channel.ChannelHandlerContext;

/**
 * 请求处理回调函数
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:35
 */
public interface IResponseCallback {

    /**
     * @description: 成功响应回调流程
     * @param:
     * @return:
     * @date: 2022/05/18 18:07:46
     */
    void onSuccess(final IRemotingResponse response, final ChannelHandlerContext context);

    /**
     * @description: 异常响应回调流程
     * @param:
     * @return:
     * @date: 2022/09/09 14:31:01
     */
    void onException(final IRemotingRequest request, final Exception ex, final ChannelHandlerContext context);
}
