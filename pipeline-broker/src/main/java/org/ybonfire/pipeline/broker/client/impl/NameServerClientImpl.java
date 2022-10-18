package org.ybonfire.pipeline.broker.client.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.ybonfire.pipeline.broker.client.INameServerClient;
import org.ybonfire.pipeline.broker.model.heartbeat.HeartbeatData;
import org.ybonfire.pipeline.broker.model.topic.PartitionConfig;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.model.PartitionConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.BrokerHeartbeatRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Nameserver远程调用客户端接口
 *
 * @author yuanbo
 * @date 2022-09-23 10:57
 */
public class NameServerClientImpl extends NettyRemotingClient implements INameServerClient {
    public NameServerClientImpl() {}

    /**
     * 注册ResponseProcessors
     */
    @Override
    protected void registerResponseProcessors() {}

    /**
     * 上报心跳
     *
     * @param heartbeatData 心跳数据
     * @param address 地址
     * @param timeoutMillis 超时,米尔斯
     */
    @Override
    public void heartbeat(final HeartbeatData heartbeatData, final String address, final long timeoutMillis) {
        final IRemotingRequest<BrokerHeartbeatRequest> request = buildBrokerHeartbeatRequest(heartbeatData);
        super.request(request, address, timeoutMillis);
    }

    /**
     * @description: 构造BrokerHeartbeatRequest
     * @param:
     * @return:
     * @date: 2022/10/15 14:37:40
     */
    private IRemotingRequest<BrokerHeartbeatRequest> buildBrokerHeartbeatRequest(final HeartbeatData heartbeatData) {
        if (heartbeatData == null) {
            return null;
        }

        final String brokerId = heartbeatData.getBrokerId();
        final Integer role = heartbeatData.getRole().getCode();
        final String address = heartbeatData.getAddress();
        final List<TopicConfigRemotingEntity> topicConfigs =
            CollectionUtils.emptyIfNull(heartbeatData.getTopicConfigs()).stream()
                .map(this::buildTopicConfigRemotingEntity).collect(Collectors.toList());
        final boolean enableAutoCreateTopic = heartbeatData.isEnableAutoCreateTopic();
        final Long timestamp = heartbeatData.getTimestamp();

        final BrokerHeartbeatRequest body =
            BrokerHeartbeatRequest.builder().brokerId(brokerId).role(role).address(address).topicConfigs(topicConfigs)
                .enableAutoCreateTopic(enableAutoCreateTopic).timestamp(timestamp).build();

        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.BROKER_HEARTBEAT.getCode(), body);
    }

    /**
     * 构造TopicConfigRemotingEntity
     *
     * @param topicConfig 主题配置
     * @return {@link TopicConfigRemotingEntity}
     */
    private TopicConfigRemotingEntity buildTopicConfigRemotingEntity(final TopicConfig topicConfig) {
        if (topicConfig == null) {
            return null;
        }

        final String topic = topicConfig.getTopic();
        final List<PartitionConfigRemotingEntity> partitions = CollectionUtils.isEmpty(topicConfig.getPartitions())
            ? Collections.emptyList() : topicConfig.getPartitions().stream()
                .map(this::buildPartitionConfigRemotingEntity).filter(Objects::nonNull).collect(Collectors.toList());

        return new TopicConfigRemotingEntity(topic, partitions);
    }

    /**
     * 构造PartitionConfigRemotingEntity
     *
     * @param partitionConfig 分区配置
     * @return {@link PartitionConfigRemotingEntity}
     */
    private PartitionConfigRemotingEntity buildPartitionConfigRemotingEntity(final PartitionConfig partitionConfig) {
        if (partitionConfig == null) {
            return null;
        }

        return new PartitionConfigRemotingEntity(partitionConfig.getPartitionId());
    }
}
