package org.ybonfire.pipeline.nameserver.handler;

import java.util.List;

import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectAllRequest;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

/**
 * SelectAllRoute请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 14:04
 */
public final class SelectAllRouteRequestHandler
    extends AbstractNettyRemotingRequestHandler<RouteSelectAllRequest, RouteSelectResponse> {
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
    protected void check(final IRemotingRequest<RouteSelectAllRequest> request) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:13
     */
    @Override
    protected RemotingResponse<RouteSelectResponse> fire(final IRemotingRequest<RouteSelectAllRequest> request) {
        final List<TopicInfo> result = this.routeManageService.selectAll();
        return null;
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:18
     */
    @Override
    protected RemotingResponse<RouteSelectResponse> onException(final IRemotingRequest<RouteSelectAllRequest> request,
        final Exception ex) {
        return null;
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:24
     */
    @Override
    protected void onComplete(final IRemotingRequest<RouteSelectAllRequest> request) {

    }

    /**
     * @description: 构造RouteSelectResponse
     * @param:
     * @return:
     * @date: 2022/07/13 18:37:24
     */
    private RouteSelectResponse buildRouteSelectResponse(final List<TopicInfo> topicInfos) {
        // TODO
        return null;
    }
}
