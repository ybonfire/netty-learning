package org.ybonfire.pipeline.client.handler.impl;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.handler.AbstractNettyRemotingResponseHandler;
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
public class DefaultNettyRemotingResponseHandler extends AbstractNettyRemotingResponseHandler {
    private final IInternalLogger logger = new SimpleInternalLogger();
    private final InflightRequestManager inflightRequestManager;
    private final ExecutorService executorService = ThreadPoolUtil.getRequestCallbackExecutorService();

    public DefaultNettyRemotingResponseHandler(final InflightRequestManager inflightRequestManager) {
        this.inflightRequestManager = inflightRequestManager;
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    @Override
    protected void check(final RemotingCommand response, final ChannelHandlerContext context) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/09 14:02:42
     */
    @Override
    protected RemotingCommand fire(final RemotingCommand response, final ChannelHandlerContext context) {
        inflightRequestManager.get(response.getCommandId()).ifPresent(future -> handleResponse(future, response));
        return null;
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/09 14:02:45
     */
    @Override
    protected RemotingCommand onException(final RemotingCommand response, final ChannelHandlerContext context,
        final Exception ex) {
        logger.error("响应处理失败", ex);
        return null;
    }

    /**
     * @description: 处理完成流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:24:51
     */
    @Override
    protected void onComplete(final RemotingCommand response, final ChannelHandlerContext context) {
        /** 移除在途请求 **/
        inflightRequestManager.remove(response.getCommandId());
    }

    /**
     * @description: 执行响应处理流程
     * @param:
     * @return:
     * @date: 2022/06/20 19:02:12
     */
    private void handleResponse(final RemoteRequestFuture future, final RemotingCommand response) {
        if (future.isCompleted()) {
            return;
        }

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
            try {
                if (future.isRequestSuccess()) {
                    final RemotingCommand response = future.getResponseFuture().get();
                    if (response != null) {
                        callback.onSuccess(response);
                    }
                } else {
                    if (future.getCause() != null) {
                        callback.onException(future.getCause());
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                // ignore
            }
        });
    }
}
