package org.ybonfire.pipeline.nameserver.processor;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.nameserver.RouteSelectAllRequest;
import org.ybonfire.pipeline.common.protocol.response.nameserver.RouteSelectResponse;
import org.ybonfire.pipeline.nameserver.converter.TopicInfoConverter;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractRemotingRequestProcessor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SelectAllRouteRequestProcessor
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 14:04
 */
public final class SelectAllRouteRequestProcessor extends AbstractRemotingRequestProcessor<RouteSelectAllRequest> {
    private static final SelectAllRouteRequestProcessor INSTANCE = new SelectAllRouteRequestProcessor();
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final RouteManageService routeManageService = RouteManageService.getInstance();

    private SelectAllRouteRequestProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/11 14:22:58
     */
    @Override
    protected void check(final IRemotingRequest<RouteSelectAllRequest> request) {
        if (!isRouteSelectAllRequest(request)) {
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
    protected RemotingResponse fire(final IRemotingRequest<RouteSelectAllRequest> request) {
        final List<TopicInfo> result = this.routeManageService.selectAllTopicInfo();
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(),
            convert(result));
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
     * @description: 转换TopicInfo至RouteSelectResponse
     * @param:
     * @return:
     * @date: 2022/07/28 19:43:59
     */
    private RouteSelectResponse convert(final List<TopicInfo> topicInfos) {
        final Map<String,
            TopicConfigRemotingEntity> result = topicInfos.stream().map(TopicInfoConverter.getInstance()::convert)
                .filter(Objects::nonNull).collect(Collectors.toMap(TopicConfigRemotingEntity::getTopic,
                    topicInfoRemotingEntity -> topicInfoRemotingEntity));
        return RouteSelectResponse.builder().result(result).build();
    }

    /**
     * 判断是否为RouteSelectAllRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isRouteSelectAllRequest(final IRemotingRequest<RouteSelectAllRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.SELECT_ALL_ROUTE;
    }

    /**
     * 获取SelectAllRouteRequestProcessor实例
     *
     * @return {@link SelectAllRouteRequestProcessor}
     */
    public static SelectAllRouteRequestProcessor getInstance() {
        return INSTANCE;
    }
}
