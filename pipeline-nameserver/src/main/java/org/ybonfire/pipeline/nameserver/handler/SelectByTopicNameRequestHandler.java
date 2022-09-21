package org.ybonfire.pipeline.nameserver.handler;

import java.util.Collections;
import java.util.Optional;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
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
    extends AbstractNettyRemotingRequestHandler<RouteSelectByTopicRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
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
    protected RemotingResponse fire(final IRemotingRequest<RouteSelectByTopicRequest> request) {
        final Optional<TopicInfo> topicInfoOptional =
            routeManageService.selectByTopicName(request.getBody().getTopic());
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(),
            convert(topicInfoOptional));
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
        } else {
            return RouteSelectResponse.builder().result(Collections.emptyMap()).build();
        }
    }
}
