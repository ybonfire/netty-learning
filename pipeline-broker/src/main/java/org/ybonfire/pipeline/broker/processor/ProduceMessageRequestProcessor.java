package org.ybonfire.pipeline.broker.processor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.broker.exception.BrokerNotPartitionLeaderException;
import org.ybonfire.pipeline.broker.model.topic.PartitionConfig;
import org.ybonfire.pipeline.broker.model.RoleEnum;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.broker.role.RoleManager;
import org.ybonfire.pipeline.broker.store.message.impl.DefaultMessageStoreServiceImpl;
import org.ybonfire.pipeline.broker.topic.impl.DefaultTopicConfigManager;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.broker.MessageProduceRequest;
import org.ybonfire.pipeline.server.exception.BadRequestException;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractNettyRemotingRequestProcessor;

import java.util.List;
import java.util.Optional;

/**
 * MessageProduceRequest请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:37
 */
public final class ProduceMessageRequestProcessor extends AbstractNettyRemotingRequestProcessor<MessageProduceRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ProduceMessageRequestProcessor INSTANCE = new ProduceMessageRequestProcessor();

    private ProduceMessageRequestProcessor() {
        DefaultMessageStoreServiceImpl.getInstance().start();
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:35
     */
    @Override
    protected void check(final IRemotingRequest<MessageProduceRequest> request) {
        // 校验请求类型
        if (!isProduceMessageRequest(request)) {
            throw new RequestTypeNotSupportException();
        }

        // 校验Broker能否处理该请求
        if (!isEnableProcess()) {
            throw new BrokerNotPartitionLeaderException();
        }

        // 校验请求参数
        if (!isRequestValid(request.getBody())) {
            throw new BadRequestException();
        }
    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:41
     */
    @Override
    protected RemotingResponse fire(final IRemotingRequest<MessageProduceRequest> request) {
        final MessageProduceRequest body = request.getBody();
        DefaultMessageStoreServiceImpl.getInstance().store(body.getTopic(), body.getPartitionId(), body.getMessage());
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(), null);
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:49
     */
    @Override
    protected void onComplete(final IRemotingRequest<MessageProduceRequest> request) {

    }

    /**
     * 判断是否为MessageProduceRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isProduceMessageRequest(final IRemotingRequest<MessageProduceRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.PRODUCE_MESSAGE;
    }

    /**
     * @description: 判断Broker是否能处理该请求
     * @param:
     * @return:
     * @date: 2022/09/05 16:06:47
     */
    private boolean isEnableProcess() {
        return RoleManager.getInstance().get() == RoleEnum.LEADER;
    }

    /**
     * @description: 判断请求参数是否合法
     * @param:
     * @return:
     * @date: 2022/09/14 15:22:31
     */
    private boolean isRequestValid(final MessageProduceRequest request) {
        // check request
        if (request == null) {
            LOGGER.error("produce message request is null");
            return false;
        }

        // check message
        if (request.getMessage() == null) {
            LOGGER.error("produce message request[message] is null");
            return false;
        }

        // check payload
        if (request.getMessage().getPayload() == null || request.getMessage().getPayload().length == 0) {
            LOGGER.error("produce message request[message[payload]] is empty");
            return false;
        }

        // check topic
        if (StringUtils.isBlank(request.getTopic())) {
            LOGGER.error("produce message request[topic] is blank");
            return false;
        }

        final Optional<TopicConfig> topicConfigOptional =
            DefaultTopicConfigManager.getInstance().selectTopicConfig(request.getTopic());
        if (!topicConfigOptional.isPresent()) {
            LOGGER.error("produce message request topic config not found");
            return false;
        }

        // check partition
        if (request.getPartitionId() == null) {
            LOGGER.error("produce message request[partitionId] is null");
            return false;
        }

        final List<PartitionConfig> partitions = topicConfigOptional.get().getPartitions();
        if (CollectionUtils.isEmpty(partitions)) {
            LOGGER.error("produce message request partition config not found");
            return false;
        }

        return partitions.stream().map(PartitionConfig::getPartitionId)
            .anyMatch(partitionId -> partitionId.equals(request.getPartitionId()));
    }

    /**
     * 获取ProduceMessageRequestProcessor实例
     *
     * @return {@link ProduceMessageRequestProcessor}
     */
    public static ProduceMessageRequestProcessor getInstance() {
        return INSTANCE;
    }
}
