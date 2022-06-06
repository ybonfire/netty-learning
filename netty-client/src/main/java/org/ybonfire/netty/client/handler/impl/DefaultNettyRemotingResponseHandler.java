package org.ybonfire.netty.client.handler.impl;

import org.ybonfire.netty.client.handler.INettyRemotingResponseHandler;
import org.ybonfire.netty.client.manager.InflightRequestManager;
import org.ybonfire.netty.client.model.RemoteRequestFuture;
import org.ybonfire.netty.common.command.RemotingCommand;

import io.netty.channel.ChannelHandlerContext;
import org.ybonfire.netty.common.logger.IInternalLogger;
import org.ybonfire.netty.common.logger.impl.SimpleInternalLogger;

/**
 * 默认远程响应处理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 18:24
 */
public class DefaultNettyRemotingResponseHandler implements INettyRemotingResponseHandler {
    private final IInternalLogger logger = new SimpleInternalLogger();
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
        logger.info(response.getBody().toString());
        inflightRequestManager.get(response.getCommandId()).filter(this::isNotExpireResponse)
            .ifPresent(future -> future.complete(response));
        return null;
    }

    /**
     * @description: 判断是否是未超时响应
     * @param:
     * @return:
     * @date: 2022/06/02 10:09:09
     */
    private boolean isNotExpireResponse(final RemoteRequestFuture future) {
        return System.currentTimeMillis() <= future.getStartTimestamp() + future.getTimeoutMillis();
    }
}
