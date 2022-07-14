package org.ybonfire.pipeline.producer.client.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.ProduceResultRemotingEntity;
import org.ybonfire.pipeline.common.protocol.request.MessageProduceRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectRequest;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.producer.client.IProduceClient;
import org.ybonfire.pipeline.producer.converter.impl.NodeConverter;
import org.ybonfire.pipeline.producer.converter.impl.PartitionConverter;
import org.ybonfire.pipeline.producer.converter.impl.ProduceResultConverter;
import org.ybonfire.pipeline.producer.converter.impl.TopicInfoConverter;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * Producer远程调用客户端
 *
 * @author Bo.Yuan5
 * @date 2022-07-14 14:01
 */
public final class ProducerClientImpl extends NettyRemotingClient implements IProduceClient {
    private final TopicInfoConverter topicInfoConverter =
        new TopicInfoConverter(new PartitionConverter(new NodeConverter()));
    private final ProduceResultConverter produceResultConverter = new ProduceResultConverter();

    public ProducerClientImpl(final NettyClientConfig config) {
        super(config);
    }

    /**
     * @description: 注册Producer响应处理器
     * @param:
     * @return:
     * @date: 2022/07/14 14:02:24
     */
    @Override
    protected void registerResponseHandlers() {}

    /**
     * @description: 发送查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:02
     */
    @Override
    public List<TopicInfo> selectAllTopicInfo(final String address, final long timeoutMillis) {
        try {
            final RemotingCommand response = request(address, buildSelectAllTopicInfoRequest(), timeoutMillis);
            if (response.isSuccess()) {
                final RouteSelectResponse data = (RouteSelectResponse)response.getBody();
                return MapUtils.isEmpty(data.getResult()) ? Collections.emptyList()
                    : data.getResult().values().stream().map(topicInfoConverter::convert).collect(Collectors.toList());
            } else { // 远程调用响应异常
                // TODO
                return Collections.emptyList();
            }
        } catch (Exception ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.REMOTING_INVOKE_FAILED);
        }
    }

    /**
     * @description: 发送查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:04
     */
    @Override
    public Optional<TopicInfo> selectTopicInfo(final String topic, final String address, final long timeoutMillis) {
        try {
            final RemotingCommand response = request(address, buildSelectTopicInfoRequest(topic), timeoutMillis);
            if (response.isSuccess()) {
                final RouteSelectResponse data = (RouteSelectResponse)response.getBody();

                return MapUtils.isEmpty(data.getResult()) ? Optional.empty()
                    : Optional.ofNullable(data.getResult().get(topic)).map(topicInfoConverter::convert);
            } else { // 远程调用响应异常
                // TODO
                return Optional.empty();
            }
        } catch (Exception ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.REMOTING_INVOKE_FAILED);
        }
    }

    /**
     * @description: 投递消息
     * @param:
     * @return:
     * @date: 2022/06/30 10:44:03
     */
    @Override
    public ProduceResult produce(final MessageWrapper message, final String address, final long timeoutMillis) {
        try {
            final RemotingCommand response =
                request(address, buildProduceMessageRequest(message, address), timeoutMillis);
            return produceResultConverter.convert((ProduceResultRemotingEntity)response.getBody());
        } catch (Exception ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.REMOTING_INVOKE_FAILED);
        }
    }

    /**
     * @description: 构造查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:13:21
     */
    private RemotingCommand buildSelectAllTopicInfoRequest() {
        return RemotingCommand.createRequestCommand(RequestEnum.SELECT_ALL_ROUTE.getCode(),
            UUID.randomUUID().toString(), null);
    }

    /**
     * @description: 构造查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:13:30
     */
    private RemotingCommand buildSelectTopicInfoRequest(final String topic) {
        return RemotingCommand.createRequestCommand(RequestEnum.SELECT_ROUTE.getCode(), UUID.randomUUID().toString(),
            RouteSelectRequest.builder().topic(topic).build());
    }

    /**
     * @description: 构造消息生产请求
     * @param:
     * @return:
     * @date: 2022/06/30 10:45:21
     */
    private RemotingCommand buildProduceMessageRequest(final MessageWrapper messageWrapper, final String address) {
        final String topic = messageWrapper.getMessage().getTopic();
        final int partitionId = messageWrapper.getPartition().getPartitionId();
        final Message message = messageWrapper.getMessage();

        return RemotingCommand.createRequestCommand(RequestEnum.PRODUCER_SEND_MESSAGE.getCode(),
            UUID.randomUUID().toString(), MessageProduceRequest.builder().topic(topic).partitionId(partitionId)
                .address(address).message(message).build());
    }
}
