package org.ybonfire.pipeline.nameserver.processor;

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
import org.ybonfire.pipeline.common.protocol.request.nameserver.BrokerHeartbeatRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.nameserver.replica.publish.RouteUploadRequestPublisher;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.nameserver.route.impl.InMemoryRouteRepository;
import org.ybonfire.pipeline.server.exception.BadRequestException;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractRemotingRequestProcessor;

import java.util.List;

/**
 * BrokerHeartbeatRequest请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:35
 */
public final class BrokerHeartbeatRequestProcessor extends AbstractRemotingRequestProcessor<BrokerHeartbeatRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final BrokerHeartbeatRequestProcessor INSTANCE = new BrokerHeartbeatRequestProcessor();
    private final RouteManageService routeManageService = new RouteManageService(InMemoryRouteRepository.getInstance());
    private final RouteUploadRequestPublisher uploadRouteRequestPublisher = RouteUploadRequestPublisher.getInstance();

    private BrokerHeartbeatRequestProcessor() {
        this.uploadRouteRequestPublisher.start();
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    @Override
    protected void check(final IRemotingRequest<BrokerHeartbeatRequest> request) {
        if (!isBrokerHeartbeatRequest(request)) {
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
    protected RemotingResponse fire(final IRemotingRequest<BrokerHeartbeatRequest> request) {
        routeManageService.uploadByBroker(request.getBody());
        uploadRouteRequestPublisher.publish(request);

        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(),
            new DefaultResponse(ResponseEnum.SUCCESS.name()));
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    @Override
    protected void onComplete(IRemotingRequest<BrokerHeartbeatRequest> request) {

    }

    /**
     * 判断是否为BrokerHeartbeatRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isBrokerHeartbeatRequest(final IRemotingRequest<BrokerHeartbeatRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.BROKER_HEARTBEAT;
    }

    /**
     * @description: 判断请求参数是否合法
     * @param:
     * @return:
     * @date: 2022/09/14 15:22:31
     */
    private boolean isRequestValid(final BrokerHeartbeatRequest request) {
        // check request
        if (request == null) {
            LOGGER.error("broker heartbeat request is null");
            return false;
        }

        // check address
        if (StringUtils.isBlank(request.getAddress())) {
            LOGGER.error("broker heartbeat request[address] is invalid");
            return false;
        }

        // check topic config
        if (CollectionUtils.isNotEmpty(request.getTopicConfigs())) {
            final List<TopicConfigRemotingEntity> topics = request.getTopicConfigs();
            for (final TopicConfigRemotingEntity topic : topics) {
                if (topic == null) {
                    LOGGER.error("broker heartbeat request[topicConfigs] is null");
                    return false;
                }

                if (StringUtils.isBlank(topic.getTopic())) {
                    LOGGER.error("broker heartbeat request[topicConfigs[topic]] is blank");
                    return false;
                }

                // check partition
                final List<PartitionConfigRemotingEntity> partitions = topic.getPartitions();
                if (CollectionUtils.isNotEmpty(topic.getPartitions())) {
                    for (final PartitionConfigRemotingEntity partition : partitions) {
                        if (partition == null) {
                            LOGGER.error("broker heartbeat request[topicConfigs[partitions[partition]]] is null");
                            return false;
                        }

                        if (partition.getPartitionId() == null) {
                            LOGGER.error(
                                "broker heartbeat request[topicConfigs[partitions[partition[partitionId]]]] is null");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * 获取BrokerHeartbeatRequestProcessor实例
     *
     * @return {@link BrokerHeartbeatRequestProcessor}
     */
    public static BrokerHeartbeatRequestProcessor getInstance() {
        return INSTANCE;
    }
}
