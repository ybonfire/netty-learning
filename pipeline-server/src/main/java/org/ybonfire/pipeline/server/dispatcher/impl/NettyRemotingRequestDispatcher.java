package org.ybonfire.pipeline.server.dispatcher.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.server.dispatcher.IRemotingRequestDispatcher;
import org.ybonfire.pipeline.server.handler.INettyRemotingRequestHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * Netty远程调用请求分发器
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 09:46
 */
public class NettyRemotingRequestDispatcher
    implements IRemotingRequestDispatcher<ChannelHandlerContext, INettyRemotingRequestHandler> {
    private final Map<Integer, Pair<INettyRemotingRequestHandler, ExecutorService>> handlerTable =
        new ConcurrentHashMap<>();

    /**
     * @description: 请求分发
     * @param:
     * @return:
     * @date: 2022/05/19 09:47:39
     */
    @Override
    public Optional<Pair<INettyRemotingRequestHandler, ExecutorService>> dispatch(final RemotingCommand request) {
        return Optional.ofNullable(handlerTable.get(request.getCode()));
    }

    /**
     * @description: 注册请求处理器
     * @param:
     * @return:
     * @date: 2022/05/19 09:49:29
     */
    @Override
    public void registerRemotingRequestHandler(final int requestCode, final INettyRemotingRequestHandler handler,
        final ExecutorService executor) {
        if (handler == null || executor == null) {
            throw new IllegalArgumentException();
        }

        handlerTable.put(requestCode, new Pair<>(handler, executor));
    }
}
