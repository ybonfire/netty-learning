package org.ybonfire.netty.server.callback;

import io.netty.channel.ChannelHandlerContext;
import org.ybonfire.netty.common.command.RemotingCommand;

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
