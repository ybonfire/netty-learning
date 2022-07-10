package org.ybonfire.pipeline.server.handler;

import org.ybonfire.pipeline.common.command.RemotingCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * Netty远程调用请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 18:18
 */
public abstract class AbstractNettyRemotingRequestHandler implements INettyRemotingRequestHandler {

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/07/01 18:19:03
     */
    @Override
    public final RemotingCommand handle(final RemotingCommand request, final ChannelHandlerContext context) {
        // 参数校验
        check(request, context);

        try {
            // 执行业务流程
            return fire(request, context);
        } catch (final Exception ex) {
            // 执行异常处理
            return onException(request, context, ex);
        } finally {
            onComplete(request, context);
        }
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    protected abstract void check(final RemotingCommand request, final ChannelHandlerContext context);

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    protected abstract RemotingCommand fire(final RemotingCommand request, final ChannelHandlerContext context);

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:46
     */
    protected abstract RemotingCommand onException(final RemotingCommand request, final ChannelHandlerContext context,
        final Exception ex);

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    protected abstract void onComplete(final RemotingCommand request, final ChannelHandlerContext context);
}
