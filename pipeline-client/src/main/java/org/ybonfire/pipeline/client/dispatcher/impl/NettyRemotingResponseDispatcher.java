package org.ybonfire.pipeline.client.dispatcher.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.dispatcher.IRemotingResponseDispatcher;
import org.ybonfire.pipeline.client.handler.IRemotingResponseHandler;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * Netty远程调用响应分发器
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 00:00
 */
public class NettyRemotingResponseDispatcher implements IRemotingResponseDispatcher<IRemotingResponseHandler> {
    private final Map<Integer, Pair<IRemotingResponseHandler, ExecutorService>> handlerTable =
        new ConcurrentHashMap<>();

    /**
     * @description: 响应分发
     * @param:
     * @return:
     * @date: 2022/05/24 00:08:13
     */
    @Override
    public Optional<Pair<IRemotingResponseHandler, ExecutorService>> dispatch(final RemotingResponse response) {
        return Optional.ofNullable(handlerTable.get(response.getCode()));
    }

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/24 00:08:16
     */
    @Override
    public void registerRemotingRequestHandler(final int responseCode, final IRemotingResponseHandler handler,
        ExecutorService executor) {
        if (handler == null || executor == null) {
            throw new IllegalArgumentException();
        }

        handlerTable.put(responseCode, new Pair<>(handler, executor));
    }
}
