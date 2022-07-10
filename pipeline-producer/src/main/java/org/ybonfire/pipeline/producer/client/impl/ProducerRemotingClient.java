package org.ybonfire.pipeline.producer.client.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.constant.RequestCodeConstant;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.ProduceResultRemotingEntity;
import org.ybonfire.pipeline.common.protocol.TopicInfoRemotingEntity;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.producer.client.IRemotingClient;
import org.ybonfire.pipeline.producer.converter.impl.NodeConverter;
import org.ybonfire.pipeline.producer.converter.impl.PartitionConverter;
import org.ybonfire.pipeline.producer.converter.impl.ProduceResultConverter;
import org.ybonfire.pipeline.producer.converter.impl.TopicInfoConverter;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * 生产者远程调用客户端
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 17:01
 */
public class ProducerRemotingClient implements IRemotingClient {
    private final NettyRemotingClient client;
    private final TopicInfoConverter topicInfoConverter;
    private final ProduceResultConverter produceResultConverter;

    public ProducerRemotingClient(final NettyRemotingClient client) {
        this.client = client;
        this.topicInfoConverter = new TopicInfoConverter(new PartitionConverter(new NodeConverter()));
        this.produceResultConverter = new ProduceResultConverter();
    }

    /**
     * @description: 发送查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:02
     */
    @Override
    public List<TopicInfo> selectAllTopicInfo(final String address, final long timeoutMillis) {
        try {
            final RemotingCommand response = client.request(address, buildSelectAllTopicInfoRequest(), timeoutMillis);
            return ((List<TopicInfoRemotingEntity>)response.getBody()).stream().map(topicInfoConverter::convert)
                .collect(Collectors.toList());
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
            final RemotingCommand response = client.request(address, buildSelectTopicInfoRequest(topic), timeoutMillis);
            return Optional.ofNullable(topicInfoConverter.convert((TopicInfoRemotingEntity)response.getBody()));
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
                client.request(address, buildProduceMessageRequest(message.getMessage()), timeoutMillis);
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
        return RemotingCommand.createRequestCommand(RequestCodeConstant.SELECT_ALL_ROUTE_CODE,
            UUID.randomUUID().toString(), null);
    }

    /**
     * @description: 构造查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:13:30
     */
    private RemotingCommand buildSelectTopicInfoRequest(final String topic) {
        return RemotingCommand.createRequestCommand(RequestCodeConstant.SELECT_ROUTE_CODE, UUID.randomUUID().toString(),
            topic);
    }

    /**
     * @description: 构造消息生产请求
     * @param:
     * @return:
     * @date: 2022/06/30 10:45:21
     */
    private RemotingCommand buildProduceMessageRequest(final Message message) {
        return RemotingCommand.createRequestCommand(RequestCodeConstant.PRODUCE_MESSAGE_CODE,
            UUID.randomUUID().toString(), message);
    }

}
