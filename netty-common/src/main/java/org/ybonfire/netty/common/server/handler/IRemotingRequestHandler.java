package org.ybonfire.netty.common.server.handler;

import org.ybonfire.netty.common.command.RemotingCommand;

/**
 * 远程请求处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:21
 */
public interface IRemotingRequestHandler<Context> {

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/05/18 10:25:46
     */
    RemotingCommand handle(final RemotingCommand request, final Context context);
}
