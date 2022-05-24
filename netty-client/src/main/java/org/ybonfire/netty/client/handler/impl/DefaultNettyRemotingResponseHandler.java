package org.ybonfire.netty.client.handler.impl;

import org.ybonfire.netty.client.handler.INettyRemotingResponseHandler;
import org.ybonfire.netty.client.manager.InflightRequestManager;
import org.ybonfire.netty.common.command.RemotingCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * 默认远程响应处理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 18:24
 */
public class DefaultNettyRemotingResponseHandler implements INettyRemotingResponseHandler {
    private final InflightRequestManager inflightRequestManager;

    public DefaultNettyRemotingResponseHandler(final InflightRequestManager inflightRequestManager) {
        this.inflightRequestManager = inflightRequestManager;
    }

    /**
     * @description: 处理响应
     * @param:
     * @return:
     * @date: 2022/05/23 23:42:24
     */
    @Override
    public RemotingCommand handle(final RemotingCommand response, final ChannelHandlerContext context) {
        System.out.println(response.getBody());
        inflightRequestManager.get(response.getCommandId()).ifPresent(future -> future.complete(response));
        return null;
    }
}
