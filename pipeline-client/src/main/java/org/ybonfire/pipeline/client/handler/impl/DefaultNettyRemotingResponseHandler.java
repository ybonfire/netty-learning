package org.ybonfire.pipeline.client.handler.impl;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.handler.INettyRemotingResponseHandler;
import org.ybonfire.pipeline.client.manager.InflightRequestManager;
import org.ybonfire.pipeline.client.model.RemoteRequestFuture;
import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;

import io.netty.channel.ChannelHandlerContext;

/**
 * 默认远程响应处理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 18:24
 */
public class DefaultNettyRemotingResponseHandler implements INettyRemotingResponseHandler {
    private final IInternalLogger logger = new SimpleInternalLogger();
    private final InflightRequestManager inflightRequestManager;
    private final ExecutorService executorService = ThreadPoolUtil.getTestExecutorService();

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
        inflightRequestManager.get(response.getCommandId()).ifPresent(future -> handleResponse(future, response));
        return null;
    }

    /**
     * @description: 执行响应处理流程
     * @param:
     * @return:
     * @date: 2022/06/20 19:02:12
     */
    private void handleResponse(final RemoteRequestFuture future, final RemotingCommand response) {
        /** 移除在途请求 **/
        inflightRequestManager.remove(response.getCommandId());

        /** 填充响应 **/
        future.complete(response);

        /** 执行请求回调 **/
        if (!future.isExpired()) {
            Optional.ofNullable(future.getCallback()).ifPresent(callback -> invokeCallback(callback, future));
        }
    }

    /**
     * @description: 异步执行回调函数
     * @param:
     * @return:
     * @date: 2022/06/20 19:10:07
     */
    private void invokeCallback(final IRequestCallback callback, final RemoteRequestFuture future) {
        executorService.submit(() -> {
            if (future.isRequestSuccess()) {
                try {
                    final RemotingCommand response = future.getResponseFuture().get();
                    if (response != null) {
                        callback.onSuccess(response);
                    }
                } catch (Exception e) {
                    // ignore
                }
            } else {
                if (future.getCause() != null) {
                    callback.onException(future.getCause());
                }
            }
        });
    }
}
