package org.ybonfire.pipeline.client.handler;

import org.ybonfire.pipeline.common.command.RemotingCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * Netty远程响应请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-09 13:57
 */
public abstract class AbstractNettyRemotingResponseHandler implements INettyRemotingResponseHandler {

    /**
     * @description: 处理响应
     * @param:
     * @return:
     * @date: 2022/07/09 13:59:34
     */
    @Override
    public final RemotingCommand handle(final RemotingCommand response, final ChannelHandlerContext context) {
        // 参数校验
        check(response, context);

        try {
            // 执行业务流程
            return fire(response, context);
        } catch (final Exception ex) {
            // 执行异常处理
            return onException(response, context, ex);
        } finally {
            onComplete(response, context);
        }
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    protected abstract void check(final RemotingCommand response, final ChannelHandlerContext context);

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    protected abstract RemotingCommand fire(final RemotingCommand response, final ChannelHandlerContext context);

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:46
     */
    protected abstract RemotingCommand onException(final RemotingCommand response, final ChannelHandlerContext context,
        final Exception ex);

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    protected abstract void onComplete(final RemotingCommand response, final ChannelHandlerContext context);
}
