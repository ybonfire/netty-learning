package org.ybonfire.pipeline.broker.processor;

import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.broker.converter.UpdateTopicRequestConverter;
import org.ybonfire.pipeline.broker.exception.BrokerNotPartitionLeaderException;
import org.ybonfire.pipeline.broker.model.RoleEnum;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.broker.role.RoleManager;
import org.ybonfire.pipeline.broker.topic.impl.DefaultTopicConfigManager;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.broker.UpdateTopicRequest;
import org.ybonfire.pipeline.server.exception.BadRequestException;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractRemotingRequestProcessor;

/**
 * UpdateTopicRequest请求处理器
 *
 * @author yuanbo
 * @date 2022-10-14 16:55
 */
public final class UpdateTopicRequestProcessor extends AbstractRemotingRequestProcessor<UpdateTopicRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final UpdateTopicRequestProcessor INSTANCE = new UpdateTopicRequestProcessor();

    private UpdateTopicRequestProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:35
     */
    @Override
    protected void check(final IRemotingRequest<UpdateTopicRequest> request) {
        // 校验请求类型
        if (!isUpdateTopicRequest(request)) {
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
    protected RemotingResponse fire(final IRemotingRequest<UpdateTopicRequest> request) {
        final UpdateTopicRequest body = request.getBody();
        final TopicConfig topicConfig = UpdateTopicRequestConverter.getInstance().convert(body);
        DefaultTopicConfigManager.getInstance().updateTopicConfig(topicConfig);
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(), null);
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:49
     */
    @Override
    protected void onComplete(final IRemotingRequest<UpdateTopicRequest> request) {

    }

    /**
     * 判断是否为DeleteTopicRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isUpdateTopicRequest(final IRemotingRequest<UpdateTopicRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.UPDATE_TOPIC;
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
    private boolean isRequestValid(final UpdateTopicRequest request) {
        // check request
        if (request == null) {
            LOGGER.error("update topic request is null");
            return false;
        }

        // check topic
        if (StringUtils.isBlank(request.getTopic())) {
            LOGGER.error("update topic request[topic] is blank");
            return false;
        }

        // check partition
        if (request.getPartitionNums() == null || request.getPartitionNums() < 1) {
            LOGGER.error("update topic request[partitionNums] is valid");
            return false;
        }

        return true;
    }

    /**
     * 获取DeleteTopicRequestProcessor实例
     *
     * @return {@link UpdateTopicRequestProcessor}
     */
    public static UpdateTopicRequestProcessor getInstance() {
        return INSTANCE;
    }
}
