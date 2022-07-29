package org.ybonfire.pipeline.nameserver.handler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ybonfire.pipeline.common.constant.ResponseStatusEnum;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.model.TopicInfoRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectAllRequest;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;
import org.ybonfire.pipeline.nameserver.converter.TopicInfoConverter;
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
    private final TopicInfoConverter topicInfoConverter;

    public SelectAllRouteRequestHandler(final RouteManageService routeManageService,
        final TopicInfoConverter topicInfoConverter) {
        this.routeManageService = routeManageService;
        this.topicInfoConverter = topicInfoConverter;
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
        return success(request, result);
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
        return exception(request, ex);
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
     * @description: 构造处理成功响应体
     * @param:
     * @return:
     * @date: 2022/07/13 18:37:24
     */
    private RemotingResponse<RouteSelectResponse> success(final IRemotingRequest<RouteSelectAllRequest> request,
        final List<TopicInfo> topicInfos) {
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseStatusEnum.SUCCESS.getCode(),
            convert(topicInfos));
    }

    /**
     * @description: 构造处理异常响应体
     * @param:
     * @return:
     * @date: 2022/07/28 20:11:11
     */
    private RemotingResponse<RouteSelectResponse> exception(final IRemotingRequest<RouteSelectAllRequest> request,
        final Exception ex) {
        // TODO 不同Exception对应不同Status
        return RemotingResponse.create(request.getId(), request.getCode(),
            ResponseStatusEnum.INTERNAL_SYSTEM_ERROR.getCode());
    }

    /**
     * @description: 转换TopicInfo至RouteSelectResponse
     * @param:
     * @return:
     * @date: 2022/07/28 19:43:59
     */
    private RouteSelectResponse convert(final List<TopicInfo> topicInfos) {
        final Map<String, TopicInfoRemotingEntity> result =
            topicInfos.stream().map(topicInfoConverter::convert).filter(Objects::nonNull).collect(Collectors
                .toMap(TopicInfoRemotingEntity::getTopic, topicInfoRemotingEntity -> topicInfoRemotingEntity));
        return RouteSelectResponse.builder().result(result).build();
    }
}
