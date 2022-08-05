package org.ybonfire.pipeline.producer.client.impl;

import java.util.UUID;

import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.MessageProduceRequest;
import org.ybonfire.pipeline.common.protocol.response.MessageProduceResponse;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.producer.client.IBrokerClient;
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
    private final ProduceResultConverter produceResultConverter = new ProduceResultConverter();

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
    protected void registerResponseHandlers() {}

    /**
     * @description: 投递消息
     * @param:
     * @return:
     * @date: 2022/06/30 10:44:03
     */
    @Override
    public ProduceResult produce(final MessageWrapper message, final String address, final long timeoutMillis) {
        try {
            final IRemotingResponse<MessageProduceResponse> response =
                request(address, buildProduceMessageRequest(message, address), timeoutMillis);
            if (response.getStatus() == 0) {
                return produceResultConverter.convert(response.getBody());
            } else {
                return ProduceResult.builder().topic(message.getMessage().getTopic())
                    .partitionId(message.getPartition().getPartitionId()).offset(-1L).isSuccess(false)
                    .message(message.getMessage()).build();
            }
        } catch (Exception ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.REMOTING_INVOKE_FAILED);
        }
    }

    /**
     * @description: 构造消息生产请求
     * @param:
     * @return:
     * @date: 2022/06/30 10:45:21
     */
    private IRemotingRequest<MessageProduceRequest> buildProduceMessageRequest(final MessageWrapper messageWrapper,
        final String address) {
        final String topic = messageWrapper.getMessage().getTopic();
        final int partitionId = messageWrapper.getPartition().getPartitionId();
        final Message message = messageWrapper.getMessage();

        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.PRODUCER_SEND_MESSAGE.getCode(),
            MessageProduceRequest.builder().topic(topic).partitionId(partitionId).address(address).message(message)
                .build());
    }
}
