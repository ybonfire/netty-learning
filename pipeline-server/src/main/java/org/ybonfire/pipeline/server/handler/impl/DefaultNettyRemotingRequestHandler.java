package org.ybonfire.pipeline.server.handler.impl;

import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.server.handler.INettyRemotingRequestHandler;

import io.netty.channel.ChannelHandlerContext;

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
        return RemotingCommand.createResponseCommand(request.getCode(), request.getCommandId(), "success");
    }
}
