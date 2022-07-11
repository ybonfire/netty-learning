package org.ybonfire.pipeline.nameserver.handler;

import io.netty.channel.ChannelHandlerContext;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.constant.ResponseCodeConstant;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

import java.util.List;

/**
 * SelectAllRoute请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 14:04
 */
public final class SelectAllRouteRequestHandler extends AbstractNettyRemotingRequestHandler {
    private final RouteManageService routeManageService;

    public SelectAllRouteRequestHandler(final RouteManageService routeManageService) {
        this.routeManageService = routeManageService;
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/11 14:22:58
     */
    @Override
    protected void check(final RemotingCommand request, final ChannelHandlerContext context) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:13
     */
    @Override
    protected RemotingCommand fire(final RemotingCommand request, final ChannelHandlerContext context) {
        final List<TopicInfo> result = this.routeManageService.selectAll();
        return RemotingCommand.createResponseCommand(request.getCode(), request.getCommandId(), result);
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:18
     */
    @Override
    protected RemotingCommand onException(final RemotingCommand request, final ChannelHandlerContext context,
        final Exception ex) {
        return RemotingCommand.createResponseCommand(ResponseCodeConstant.INTERNAL_SYSTEM_ERROR, request.getCommandId(),
            "failed");
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:24
     */
    @Override
    protected void onComplete(final RemotingCommand request, final ChannelHandlerContext context) {

    }
}
