package org.ybonfire.pipeline.producer.client.impl;

import java.util.UUID;

import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.MessageProduceRequest;
import org.ybonfire.pipeline.common.protocol.response.MessageProduceResponse;
import org.ybonfire.pipeline.producer.client.IBrokerClient;
import org.ybonfire.pipeline.producer.constant.ProducerConstant;
import org.ybonfire.pipeline.producer.converter.ProduceResultConverter;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * Producer远程调用客户端
 *
 * @author Bo.Yuan5
 * @date 2022-07-14 14:01
 */
public final class BrokerClientImpl extends NettyRemotingClient implements IBrokerClient {

    public BrokerClientImpl() {
        this(new NettyClientConfig());
    }

    public BrokerClientImpl(final NettyClientConfig config) {
        super(config);
    }

    /**
     * @description: 注册Broker响应处理器
     * @param:
     * @return:
     * @date: 2022/07/14 14:02:24
     */
    @Override
    protected void registerResponseProcessors() {}

    /**
     * @description: 投递消息
     * @param:
     * @return:
     * @date: 2022/06/30 10:44:03
     */
    @Override
    public ProduceResult produce(final MessageWrapper message, final String address, final long timeoutMillis) {
        final IRemotingResponse response = super.request(address, buildProduceMessageRequest(message), timeoutMillis);
        if (response.getStatus() == ResponseEnum.SUCCESS.getCode()) {
            final MessageProduceResponse data = (MessageProduceResponse)response.getBody();
            return ProduceResultConverter.getINSTANCE().convert(data);
        } else {
            return ProduceResult.builder().topic(message.getMessage().getTopic())
                .partitionId(message.getPartition().getPartitionId()).offset(-1L).isSuccess(false)
                .message(message.getMessage()).build();
        }
    }

    /**
     * @description: 构造消息生产请求
     * @param:
     * @return:
     * @date: 2022/06/30 10:45:21
     */
    private IRemotingRequest<MessageProduceRequest> buildProduceMessageRequest(final MessageWrapper messageWrapper) {
        final String topic = messageWrapper.getMessage().getTopic();
        final int partitionId = messageWrapper.getPartition().getPartitionId();
        final Message message = messageWrapper.getMessage();
        final MessageProduceRequest request =
            MessageProduceRequest.builder().topic(topic).partitionId(partitionId).message(message).build();

        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.PRODUCE_MESSAGE.getCode(), request);
    }
}
