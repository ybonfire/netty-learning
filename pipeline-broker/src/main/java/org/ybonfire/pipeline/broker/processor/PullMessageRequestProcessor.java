package org.ybonfire.pipeline.broker.processor;

import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.broker.converter.SelectMessageResultConverter;
import org.ybonfire.pipeline.broker.exception.BrokerNotPartitionLeaderException;
import org.ybonfire.pipeline.broker.model.RoleEnum;
import org.ybonfire.pipeline.broker.model.message.SelectMessageResult;
import org.ybonfire.pipeline.broker.model.topic.PartitionConfig;
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
import org.ybonfire.pipeline.common.protocol.request.broker.PullMessageRequest;
import org.ybonfire.pipeline.server.exception.BadRequestException;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractRemotingRequestProcessor;

import java.util.List;
import java.util.Optional;

/**
 * PullMessageRequestProcessor
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:37
 */
public final class PullMessageRequestProcessor extends AbstractRemotingRequestProcessor<PullMessageRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final PullMessageRequestProcessor INSTANCE = new PullMessageRequestProcessor();

    private PullMessageRequestProcessor() {}

    /**
     * 获取ConsumeMessageRequestProcessor实例
     *
     * @return {@link PullMessageRequestProcessor}
     */
    public static PullMessageRequestProcessor getInstance() {
        return INSTANCE;
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/10/20 23:15:35
     */
    @Override
    protected void check(final IRemotingRequest<PullMessageRequest> request) {
        // 校验请求类型
        if (!isPullMessageRequest(request)) {
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
     * @date: 2022/10/20 23:15:35
     */
    @Override
    protected RemotingResponse fire(final IRemotingRequest<PullMessageRequest> request) {
        final PullMessageRequest body = request.getBody();
        final String topic = body.getTopic();
        final int partitionId = body.getPartitionId();
        final int startOffset = body.getPullStartOffset();
        final int selectCount = body.getMessageNums();
        final SelectMessageResult result =
            selectMessageByPullMessageRequest(topic, partitionId, startOffset, selectCount);

        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(),
            SelectMessageResultConverter.getInstance().convert(result));
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/10/20 23:15:35
     */
    @Override
    protected void onComplete(final IRemotingRequest<PullMessageRequest> request) {

    }

    /**
     * @description: 以某个逻辑偏移量
     * @param:
     * @return:
     * @date: 2022/10/20 23:26:48
     */
    private SelectMessageResult selectMessageByPullMessageRequest(final String topic, final int partitionId,
        final int logicOffset, final int selectCount) {
        return DefaultMessageStoreServiceImpl.getInstance().select(topic, partitionId, logicOffset, selectCount);
    }

    /**
     * @description: 判断是否为PullMessageRequest
     * @param:
     * @return:
     * @date: 2022/10/20 23:22:32
     */
    private boolean isPullMessageRequest(final IRemotingRequest<PullMessageRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.PULL_MESSAGE;
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
     * 判断请求参数是否合法
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isRequestValid(final PullMessageRequest request) {
        // check request
        if (request == null) {
            LOGGER.error("pull message request is null");
            return false;
        }

        // check topic
        if (StringUtils.isBlank(request.getTopic())) {
            LOGGER.error("pull message request[topic] is blank");
            return false;
        }

        // check partition
        if (request.getPartitionId() == null) {
            LOGGER.error("pull message request[partitionId] is null");
            return false;
        }

        final Optional<TopicConfig> topicConfigOptional =
            DefaultTopicConfigManager.getInstance().selectTopicConfig(request.getTopic());
        if (!topicConfigOptional.isPresent()) {
            LOGGER.error("pull message request topic config not found");
            return false;
        }

        final List<PartitionConfig> partitions = topicConfigOptional.get().getPartitions();
        if (partitions.stream().map(PartitionConfig::getPartitionId)
            .noneMatch(partitionId -> partitionId.equals(request.getPartitionId()))) {
            LOGGER.error("pull message request partition config not found");
            return false;
        }

        // check startOffset
        if (request.getPullStartOffset() == null || request.getPullStartOffset() < 0) {
            LOGGER.error("pull message request[pullStartOffset] is invalid");
            return false;
        }

        // check messageNums
        if (request.getMessageNums() == null || request.getMessageNums() < 0) {
            LOGGER.error("pull message request[messageNums] is invalid");
            return false;
        }

        return true;
    }

}
