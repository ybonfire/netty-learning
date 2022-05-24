package org.ybonfire.netty.common.handler;

import org.ybonfire.netty.common.command.RemotingCommand;

/**
 * 请求、响应处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 17:46
 */
@FunctionalInterface
public interface IRemotingRequestResponseHandler<Context> {

    /**
     * @description: 处理请求、响应
     * @param:
     * @return:
     * @date: 2022/05/23 17:51:33
     */
    RemotingCommand handle(final RemotingCommand command, final Context context);
}
