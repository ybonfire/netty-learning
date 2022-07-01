package org.ybonfire.pipeline.client.dispatcher.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.dispatcher.IRemotingResponseDispatcher;
import org.ybonfire.pipeline.client.handler.INettyRemotingResponseHandler;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.model.Pair;

import io.netty.channel.ChannelHandlerContext;

/**
 * Netty远程调用响应分发器
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 00:00
 */
public class NettyRemotingResponseDispatcher
    implements IRemotingResponseDispatcher<ChannelHandlerContext, INettyRemotingResponseHandler> {
    private final Map<Integer, Pair<INettyRemotingResponseHandler, ExecutorService>> handlerTable =
        new ConcurrentHashMap<>();

    /**
     * @description: 响应分发
     * @param:
     * @return:
     * @date: 2022/05/24 00:08:13
     */
    @Override
    public Optional<Pair<INettyRemotingResponseHandler, ExecutorService>> dispatch(final RemotingCommand response) {
        return Optional.ofNullable(handlerTable.get(response.getCode()));
    }

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/24 00:08:16
     */
    @Override
    public void registerRemotingRequestHandler(final int responseCode, final INettyRemotingResponseHandler handler,
        ExecutorService executor) {
        if (handler == null || executor == null) {
            throw new IllegalArgumentException();
        }

        handlerTable.put(responseCode, new Pair<>(handler, executor));
    }
}
