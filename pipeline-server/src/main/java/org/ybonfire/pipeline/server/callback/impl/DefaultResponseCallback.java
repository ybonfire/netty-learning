package org.ybonfire.pipeline.server.callback.impl;

import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.server.callback.IResponseCallback;

import io.netty.channel.ChannelHandlerContext;

/**
 * 默认请求处理回调
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:41
 */
public class DefaultResponseCallback implements IResponseCallback {

    /**
     * @description: 回调流程
     * @param:
     * @return:
     * @date: 2022/05/18 18:07:46
     */
    @Override
    public void callback(final IRemotingResponse response, final ChannelHandlerContext context) {
        context.writeAndFlush(response);
    }
}
