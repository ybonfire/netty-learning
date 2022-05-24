package org.ybonfire.netty.client.handler;

import org.ybonfire.netty.common.handler.IRemotingRequestResponseHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * Netty响应处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:29
 */
@FunctionalInterface
public interface INettyRemotingResponseHandler extends IRemotingRequestResponseHandler<ChannelHandlerContext> {}
