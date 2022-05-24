package org.ybonfire.netty.server.handler;

import org.ybonfire.netty.common.handler.IRemotingRequestResponseHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * Netty请求处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:29
 */
@FunctionalInterface
public interface INettyRemotingRequestHandler extends IRemotingRequestResponseHandler<ChannelHandlerContext> {}
