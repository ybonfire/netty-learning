package org.ybonfire.netty.server.callback.impl;

import io.netty.channel.ChannelHandlerContext;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.server.callback.IResponseCallback;

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
    public void callback(final ChannelHandlerContext context, final RemotingCommand response) {
        context.writeAndFlush(response);
    }
}
