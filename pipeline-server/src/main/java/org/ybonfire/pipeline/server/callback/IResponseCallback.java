package org.ybonfire.pipeline.server.callback;

import org.ybonfire.pipeline.common.command.RemotingCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * 请求处理回调函数
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:35
 */
public interface IResponseCallback {

    /**
     * @description: 回调流程
     * @param:
     * @return:
     * @date: 2022/05/18 18:07:46
     */
    void callback(final ChannelHandlerContext context, final RemotingCommand response);
}
