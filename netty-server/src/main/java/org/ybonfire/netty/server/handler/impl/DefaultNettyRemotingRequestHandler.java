package org.ybonfire.netty.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.server.handler.INettyRemotingRequestHandler;

/**
 * 默认远程请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 18:24
 */
public class DefaultNettyRemotingRequestHandler implements INettyRemotingRequestHandler {

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/05/23 23:42:24
     */
    @Override
    public RemotingCommand handle(final RemotingCommand request, final ChannelHandlerContext context) {
        System.out.println(request.getBody());
        return RemotingCommand.createResponseCommand(request.getCode(), "success", request.getCommandId());
    }
}
