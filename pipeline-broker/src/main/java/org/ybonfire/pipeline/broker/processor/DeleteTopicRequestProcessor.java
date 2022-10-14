package org.ybonfire.pipeline.broker.processor;

import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.broker.exception.BrokerNotPartitionLeaderException;
import org.ybonfire.pipeline.broker.model.RoleEnum;
import org.ybonfire.pipeline.broker.role.RoleManager;
import org.ybonfire.pipeline.broker.topic.impl.DefaultTopicConfigManager;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.broker.DeleteTopicRequest;
import org.ybonfire.pipeline.server.exception.BadRequestException;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractNettyRemotingRequestProcessor;

/**
 * DeleteTopicRequest请求处理器
 *
 * @author yuanbo
 * @date 2022-10-14 16:55
 */
public final class DeleteTopicRequestProcessor extends AbstractNettyRemotingRequestProcessor<DeleteTopicRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final DeleteTopicRequestProcessor INSTANCE = new DeleteTopicRequestProcessor();

    private DeleteTopicRequestProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:35
     */
    @Override
    protected void check(final IRemotingRequest<DeleteTopicRequest> request) {
        // 校验请求类型
        if (!isDeleteTopicRequest(request)) {
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
    protected RemotingResponse fire(final IRemotingRequest<DeleteTopicRequest> request) {
        final DeleteTopicRequest body = request.getBody();
        DefaultTopicConfigManager.getInstance().deleteTopicConfig(body.getTopic());
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(), null);
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:49
     */
    @Override
    protected void onComplete(final IRemotingRequest<DeleteTopicRequest> request) {

    }

    /**
     * 判断是否为DeleteTopicRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isDeleteTopicRequest(final IRemotingRequest<DeleteTopicRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.DELETE_TOPIC;
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
    private boolean isRequestValid(final DeleteTopicRequest request) {
        // check request
        if (request == null) {
            LOGGER.error("delete topic request is null");
            return false;
        }

        // check topic
        if (StringUtils.isBlank(request.getTopic())) {
            LOGGER.error("delete topic request[topic] is blank");
            return false;
        }

        return true;
    }

    /**
     * 获取DeleteTopicRequestProcessor实例
     *
     * @return {@link DeleteTopicRequestProcessor}
     */
    public static DeleteTopicRequestProcessor getInstance() {
        return INSTANCE;
    }
}
