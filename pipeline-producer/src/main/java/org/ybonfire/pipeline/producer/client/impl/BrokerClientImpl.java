package org.ybonfire.pipeline.producer.client.impl;

import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.broker.SendMessageRequest;
import org.ybonfire.pipeline.common.protocol.response.broker.SendMessageResponse;
import org.ybonfire.pipeline.producer.client.IBrokerClient;
import org.ybonfire.pipeline.producer.converter.ProduceResultConverter;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;

import java.util.UUID;

/**
 * Producer远程调用客户端
 *
 * @author Bo.Yuan5
 * @date 2022-07-14 14:01
 */
public final class BrokerClientImpl extends NettyRemotingClient implements IBrokerClient {

    public BrokerClientImpl() {}

    /**
     * @description: 投递消息
     * @param:
     * @return:
     * @date: 2022/06/30 10:44:03
     */
    @Override
    public ProduceResult produce(final MessageWrapper message, final String address, final long timeoutMillis) {
        final IRemotingResponse response = super.request(buildProduceMessageRequest(message), address, timeoutMillis);
        if (response.getStatus() == ResponseEnum.SUCCESS.getCode()) {
            final SendMessageResponse data = (SendMessageResponse)response.getBody();
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
    private IRemotingRequest<SendMessageRequest> buildProduceMessageRequest(final MessageWrapper messageWrapper) {
        final String topic = messageWrapper.getMessage().getTopic();
        final int partitionId = messageWrapper.getPartition().getPartitionId();
        final Message message = messageWrapper.getMessage();
        final SendMessageRequest request =
            SendMessageRequest.builder().topic(topic).partitionId(partitionId).message(message).build();

        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.SEND_MESSAGE.getCode(), request);
    }
}
