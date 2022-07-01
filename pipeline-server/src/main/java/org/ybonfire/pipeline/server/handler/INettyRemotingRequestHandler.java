package org.ybonfire.pipeline.server.handler;

import org.ybonfire.pipeline.common.handler.IRemotingRequestResponseHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * Netty请求处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:29
 */
@FunctionalInterface
public interface INettyRemotingRequestHandler extends IRemotingRequestResponseHandler<ChannelHandlerContext> {}
