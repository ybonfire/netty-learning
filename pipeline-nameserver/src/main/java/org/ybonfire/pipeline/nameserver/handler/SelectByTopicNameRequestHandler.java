package org.ybonfire.pipeline.nameserver.handler;

import java.util.Optional;

import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.constant.ResponseCodeConstant;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

import io.netty.channel.ChannelHandlerContext;

/**
 * SelectByTopicName请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 14:04
 */
public final class SelectByTopicNameRequestHandler extends AbstractNettyRemotingRequestHandler {
    private final RouteManageService routeManageService;

    public SelectByTopicNameRequestHandler(final RouteManageService routeManageService) {
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
        final RouteSelectRequest data = (RouteSelectRequest)request.getBody();
        final Optional<TopicInfo> result = this.routeManageService.selectByTopicName(data.getTopic());
        return RemotingCommand.createResponseCommand(request.getCode(), request.getCommandId(),
            buildRouteSelectResponse(result.orElse(null)));
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
        return RemotingCommand.createResponseCommand(ResponseEnum.INTERNAL_SYSTEM_ERROR.getCode(),
            request.getCommandId(), DefaultResponse.create("failed"));
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

    /**
     * @description: 构造RouteSelectResponse
     * @param:
     * @return:
     * @date: 2022/07/13 18:37:24
     */
    private RouteSelectResponse buildRouteSelectResponse(final TopicInfo topicInfo) {
        // TODO
        return null;
    }
}
