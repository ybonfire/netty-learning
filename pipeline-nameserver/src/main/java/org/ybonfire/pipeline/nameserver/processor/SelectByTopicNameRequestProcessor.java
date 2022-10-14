package org.ybonfire.pipeline.nameserver.processor;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.nameserver.RouteSelectByTopicRequest;
import org.ybonfire.pipeline.common.protocol.response.nameserver.RouteSelectResponse;
import org.ybonfire.pipeline.nameserver.converter.TopicInfoConverter;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.nameserver.route.impl.InMemoryRouteRepository;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractNettyRemotingRequestProcessor;

import java.util.Collections;
import java.util.Optional;

/**
 * RouteSelectByTopicRequest请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 14:04
 */
public final class SelectByTopicNameRequestProcessor
    extends AbstractNettyRemotingRequestProcessor<RouteSelectByTopicRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final SelectByTopicNameRequestProcessor INSTANCE = new SelectByTopicNameRequestProcessor();
    private final RouteManageService routeManageService = new RouteManageService(InMemoryRouteRepository.getInstance());

    private SelectByTopicNameRequestProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/11 14:22:58
     */
    @Override
    protected void check(final IRemotingRequest<RouteSelectByTopicRequest> request) {
        if (!isRouteSelectByTopicRequest(request)) {
            throw new RequestTypeNotSupportException();
        }
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
        final Optional<TopicConfigRemotingEntity> topicInfoRemotingEntityOptional =
            topicInfoOptional.map(TopicInfoConverter.getInstance()::convert);
        if (topicInfoRemotingEntityOptional.isPresent()) {
            final TopicConfigRemotingEntity topicConfigRemotingEntity = topicInfoRemotingEntityOptional.get();
            return RouteSelectResponse.builder()
                .result(Collections.singletonMap(topicConfigRemotingEntity.getTopic(), topicConfigRemotingEntity))
                .build();
        } else {
            return RouteSelectResponse.builder().result(Collections.emptyMap()).build();
        }
    }

    /**
     * 判断是否为RouteSelectByTopicRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isRouteSelectByTopicRequest(final IRemotingRequest<RouteSelectByTopicRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.SELECT_ROUTE;
    }

    /**
     * 获取SelectByTopicNameRequestProcessor实例
     *
     * @return {@link SelectByTopicNameRequestProcessor}
     */
    public static SelectByTopicNameRequestProcessor getInstance() {
        return INSTANCE;
    }
}
