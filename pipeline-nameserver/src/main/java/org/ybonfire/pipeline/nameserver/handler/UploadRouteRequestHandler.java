package org.ybonfire.pipeline.nameserver.handler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.PartitionConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.nameserver.replica.publish.RouteUploadRequestPublisher;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.server.exception.BadRequestException;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

/**
 * UploadRoute请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:35
 */
public final class UploadRouteRequestHandler extends AbstractNettyRemotingRequestHandler<RouteUploadRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final RouteManageService routeManageService;
    private final RouteUploadRequestPublisher uploadRouteRequestPublisher;

    public UploadRouteRequestHandler(final RouteManageService routeManageService,
        final RouteUploadRequestPublisher uploadRouteRequestPublisher) {
        this.routeManageService = routeManageService;
        this.uploadRouteRequestPublisher = uploadRouteRequestPublisher;
        this.uploadRouteRequestPublisher.start();
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    @Override
    protected void check(final IRemotingRequest<RouteUploadRequest> request) {
        if (!isRouteUploadRequest(request)) {
            throw new RequestTypeNotSupportException();
        }

        if (!isRequestValid(request.getBody())) {
            throw new BadRequestException();
        }
    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    @Override
    protected RemotingResponse fire(final IRemotingRequest<RouteUploadRequest> request) {
        routeManageService.uploadByBroker(request.getBody());
        uploadRouteRequestPublisher.publish(request);

        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(),
            DefaultResponse.create(ResponseEnum.SUCCESS.name()));
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    @Override
    protected void onComplete(IRemotingRequest<RouteUploadRequest> request) {

    }

    /**
     * 判断是否为RouteUploadRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isRouteUploadRequest(final IRemotingRequest<RouteUploadRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.UPLOAD_ROUTE;
    }

    /**
     * @description: 判断请求参数是否合法
     * @param:
     * @return:
     * @date: 2022/09/14 15:22:31
     */
    private boolean isRequestValid(final RouteUploadRequest request) {
        // check request
        if (request == null) {
            LOGGER.error("upload route request is null");
            return false;
        }

        // check address
        if (StringUtils.isBlank(request.getAddress())) {
            LOGGER.error("upload route request[address] is invalid");
            return false;
        }

        // check topic
        if (CollectionUtils.isNotEmpty(request.getTopics())) {
            final List<TopicConfigRemotingEntity> topics = request.getTopics();
            for (final TopicConfigRemotingEntity topic : topics) {
                if (topic == null) {
                    LOGGER.error("upload route request[topics] is null");
                    return false;
                }

                if (StringUtils.isBlank(topic.getTopic())) {
                    LOGGER.error("upload route request[topics[topic]] is blank");
                    return false;
                }

                // check partition
                final List<PartitionConfigRemotingEntity> partitions = topic.getPartitions();
                if (CollectionUtils.isNotEmpty(topic.getPartitions())) {
                    for (final PartitionConfigRemotingEntity partition : partitions) {
                        if (partition == null) {
                            LOGGER.error("upload route request[topics[partitions[partition]]] is null");
                            return false;
                        }

                        if (partition.getPartitionId() == null) {
                            LOGGER.error("upload route request[topics[partitions[partition[partitionId]]]] is null");
                            return false;
                        }

                        if (StringUtils.isBlank(partition.getAddress())) {
                            LOGGER.error("upload route request[topics[partitions[partition[address]]]] is blank");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

}
