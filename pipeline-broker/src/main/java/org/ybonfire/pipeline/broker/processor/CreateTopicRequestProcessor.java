package org.ybonfire.pipeline.broker.processor;

import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.broker.converter.CreateTopicRequestConverter;
import org.ybonfire.pipeline.broker.model.TopicConfig;
import org.ybonfire.pipeline.broker.topic.TopicConfigManager;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.CreateTopicRequest;
import org.ybonfire.pipeline.server.exception.BadRequestException;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractNettyRemotingRequestProcessor;

/**
 * 管理请求处理器
 *
 * @author yuanbo
 * @date 2022-09-21 14:50
 */
public class CreateTopicRequestProcessor extends AbstractNettyRemotingRequestProcessor<CreateTopicRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final CreateTopicRequestProcessor INSTANCE = new CreateTopicRequestProcessor();

    private CreateTopicRequestProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:35
     */
    @Override
    protected void check(final IRemotingRequest<CreateTopicRequest> request) {
        // 校验请求类型
        if (!isCreateTopicRequest(request)) {
            throw new RequestTypeNotSupportException();
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
    protected RemotingResponse fire(final IRemotingRequest<CreateTopicRequest> request) {
        final CreateTopicRequest body = request.getBody();
        final TopicConfig topicConfig = CreateTopicRequestConverter.getInstance().convert(body);
        TopicConfigManager.getInstance().updateTopicConfig(topicConfig);
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(), null);
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:49
     */
    @Override
    protected void onComplete(final IRemotingRequest<CreateTopicRequest> request) {

    }

    /**
     * 判断是否为CreateTopicRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isCreateTopicRequest(final IRemotingRequest<CreateTopicRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.UPLOAD_ROUTE;
    }

    /**
     * 判断请求参数是否合法
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isRequestValid(final CreateTopicRequest request) {
        // check request
        if (request == null) {
            LOGGER.error("create topic request is null");
            return false;
        }

        // check topic
        if (StringUtils.isBlank(request.getTopic())) {
            LOGGER.error("create topic request[topic] is blank");
            return false;
        }

        // check partition
        if (request.getPartitionNums() == null || request.getPartitionNums() < 1) {
            LOGGER.error("create topic request[partitionNums] is valid");
            return false;
        }

        return true;
    }

    /**
     * 获取CreateTopicRequestProcessor实例
     *
     * @return {@link CreateTopicRequestProcessor}
     */
    public static CreateTopicRequestProcessor getInstance() {
        return INSTANCE;
    }
}
