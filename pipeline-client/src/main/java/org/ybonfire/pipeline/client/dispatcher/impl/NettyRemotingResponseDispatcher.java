package org.ybonfire.pipeline.client.dispatcher.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.dispatcher.IRemotingResponseDispatcher;
import org.ybonfire.pipeline.client.processor.IRemotingResponseProcessor;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * Netty远程调用响应分发器
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 00:00
 */
public class NettyRemotingResponseDispatcher implements IRemotingResponseDispatcher<IRemotingResponseProcessor> {
    private static final NettyRemotingResponseDispatcher INSTANCE = new NettyRemotingResponseDispatcher();
    private final Map<Integer, Pair<IRemotingResponseProcessor, ExecutorService>> processorTable =
        new ConcurrentHashMap<>();

    private NettyRemotingResponseDispatcher() {}

    /**
     * @description: 响应分发
     * @param:
     * @return:
     * @date: 2022/05/24 00:08:13
     */
    @Override
    public Optional<Pair<IRemotingResponseProcessor, ExecutorService>> dispatch(final RemotingResponse response) {
        return Optional.ofNullable(processorTable.get(response.getCode()));
    }

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/24 00:08:16
     */
    @Override
    public void registerRemotingRequestProcessor(final int responseCode, final IRemotingResponseProcessor processor,
        ExecutorService executor) {
        if (processor == null || executor == null) {
            throw new IllegalArgumentException();
        }

        processorTable.put(responseCode, new Pair<>(processor, executor));
    }

    /**
     * 获取NettyRemotingResponseDispatcher实例
     *
     * @return {@link NettyRemotingResponseDispatcher}
     */
    public static NettyRemotingResponseDispatcher getInstance() {
        return INSTANCE;
    }
}
