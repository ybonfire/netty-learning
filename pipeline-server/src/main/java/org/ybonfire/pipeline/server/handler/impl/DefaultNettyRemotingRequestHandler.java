package org.ybonfire.pipeline.server.handler.impl;

import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * 默认远程请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 18:24
 */
public class DefaultNettyRemotingRequestHandler extends AbstractNettyRemotingRequestHandler {

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:12:54
     */
    @Override
    protected void check(final RemotingCommand request, final ChannelHandlerContext context) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/09 15:13:01
     */
    @Override
    protected RemotingCommand fire(final RemotingCommand request, final ChannelHandlerContext context) {
        return RemotingCommand.createResponseCommand(request.getCode(), request.getCommandId(),
            DefaultResponse.create("success"));
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/09 15:13:14
     */
    @Override
    protected RemotingCommand onException(final RemotingCommand request, final ChannelHandlerContext context,
        final Exception ex) {
        return null;
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    @Override
    protected void onComplete(final RemotingCommand request, final ChannelHandlerContext context) {

    }
}
