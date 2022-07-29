package org.ybonfire.pipeline.nameserver.handler;

import java.util.Collections;
import java.util.Optional;

import org.ybonfire.pipeline.common.constant.ResponseStatusEnum;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.model.TopicInfoRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectByTopicRequest;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;
import org.ybonfire.pipeline.nameserver.converter.TopicInfoConverter;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

/**
 * SelectByTopicName请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 14:04
 */
public final class SelectByTopicNameRequestHandler
    extends AbstractNettyRemotingRequestHandler<RouteSelectByTopicRequest, RouteSelectResponse> {
    private final RouteManageService routeManageService;
    private final TopicInfoConverter topicInfoConverter;

    public SelectByTopicNameRequestHandler(final RouteManageService routeManageService,
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
    protected void check(final IRemotingRequest<RouteSelectByTopicRequest> request) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:13
     */
    @Override
    protected RemotingResponse<RouteSelectResponse> fire(final IRemotingRequest<RouteSelectByTopicRequest> request) {
        final Optional<TopicInfo> topicInfoOptional =
            routeManageService.selectByTopicName(request.getBody().getTopic());
        return success(request, topicInfoOptional);
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:18
     */
    @Override
    protected RemotingResponse<RouteSelectResponse>
        onException(final IRemotingRequest<RouteSelectByTopicRequest> request, final Exception ex) {
        return exception(request, ex);
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/11 14:23:24
     */
    @Override
    protected void onComplete(final IRemotingRequest<RouteSelectByTopicRequest> request) {

    }

    /**
     * @description: 构造处理成功响应体
     * @param:
     * @return:
     * @date: 2022/07/13 18:37:24
     */
    private RemotingResponse<RouteSelectResponse> success(final IRemotingRequest<RouteSelectByTopicRequest> request,
        final Optional<TopicInfo> topicInfoOptional) {
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseStatusEnum.SUCCESS.getCode(),
            convert(topicInfoOptional));
    }

    /**
     * @description: 构造处理异常响应体
     * @param:
     * @return:
     * @date: 2022/07/28 20:11:11
     */
    private RemotingResponse<RouteSelectResponse> exception(final IRemotingRequest<RouteSelectByTopicRequest> request,
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
    private RouteSelectResponse convert(final Optional<TopicInfo> topicInfoOptional) {
        final Optional<TopicInfoRemotingEntity> topicInfoRemotingEntityOptional =
            topicInfoOptional.map(topicInfoConverter::convert);
        if (topicInfoRemotingEntityOptional.isPresent()) {
            final TopicInfoRemotingEntity topicInfoRemotingEntity = topicInfoRemotingEntityOptional.get();
            return RouteSelectResponse.builder()
                .result(Collections.singletonMap(topicInfoRemotingEntity.getTopic(), topicInfoRemotingEntity)).build();
        }
        return RouteSelectResponse.builder().result(Collections.emptyMap()).build();
    }
}
