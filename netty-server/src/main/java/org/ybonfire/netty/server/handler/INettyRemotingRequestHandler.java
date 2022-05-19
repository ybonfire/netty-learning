package org.ybonfire.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.ybonfire.netty.common.server.handler.IRemotingRequestHandler;

/**
 * Netty请求处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:29
 */
public interface INettyRemotingRequestHandler extends IRemotingRequestHandler<ChannelHandlerContext> {}
