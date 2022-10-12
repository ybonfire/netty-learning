package org.ybonfire.pipeline.server.dispatcher.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.server.dispatcher.IRemotingRequestDispatcher;
import org.ybonfire.pipeline.server.processor.IRemotingRequestProcessor;

/**
 * Netty远程调用请求分发器
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 09:46
 */
public class NettyRemotingRequestDispatcher implements IRemotingRequestDispatcher<IRemotingRequestProcessor> {
    private static final NettyRemotingRequestDispatcher INSTANCE = new NettyRemotingRequestDispatcher();
    private final Map<Integer, Pair<IRemotingRequestProcessor, ExecutorService>> processorTable =
        new ConcurrentHashMap<>();

    private NettyRemotingRequestDispatcher() {}

    /**
     * @description: 请求分发
     * @param:
     * @return:
     * @date: 2022/05/19 09:47:39
     */
    @Override
    public Optional<Pair<IRemotingRequestProcessor, ExecutorService>> dispatch(final IRemotingRequest request) {
        return Optional.ofNullable(processorTable.get(request.getCode()));
    }

    /**
     * @description: 注册请求处理器
     * @param:
     * @return:
     * @date: 2022/05/19 09:49:29
     */
    @Override
    public void registerRemotingRequestProcessor(final int requestCode, final IRemotingRequestProcessor processor,
        final ExecutorService executor) {
        if (processor == null || executor == null) {
            throw new IllegalArgumentException();
        }

        processorTable.put(requestCode, new Pair<>(processor, executor));
    }

    /**
     * 获取NettyRemotingRequestDispatcher实例
     *
     * @return {@link NettyRemotingRequestDispatcher}
     */
    public static NettyRemotingRequestDispatcher getInstance() {
        return INSTANCE;
    }
}
