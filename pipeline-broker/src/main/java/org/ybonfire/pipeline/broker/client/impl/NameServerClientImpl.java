package org.ybonfire.pipeline.broker.client.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.ybonfire.pipeline.broker.client.INameServerClient;
import org.ybonfire.pipeline.broker.model.topic.PartitionConfig;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.model.PartitionConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.common.util.RemotingUtil;

/**
 * Nameserver远程调用客户端接口
 *
 * @author yuanbo
 * @date 2022-09-23 10:57
 */
public class NameServerClientImpl extends NettyRemotingClient implements INameServerClient {
    public NameServerClientImpl() {
        super(new NettyClientConfig());
    }

    public NameServerClientImpl(final NettyClientConfig config) {
        super(config);
    }

    /**
     * 注册ResponseProcessors
     */
    @Override
    protected void registerResponseProcessors() {}

    @Override
    public void uploadTopicConfig(final List<TopicConfig> topicConfigs, final String address,
        final long timeoutMillis) {
        super.request(address, buildRouteUploadRequest(RemotingUtil.getLocalAddress(), topicConfigs), timeoutMillis);
    }

    /**
     * 构建RouteUploadRequest
     *
     * @param address broker地址
     * @param topicConfigs 主题配置
     * @return {@link DefaultResponse.RouteUploadRequest}
     */
    private IRemotingRequest<DefaultResponse.RouteUploadRequest> buildRouteUploadRequest(final String address,
        final List<TopicConfig> topicConfigs) {
        final List<TopicConfigRemotingEntity> topics =
            CollectionUtils.isEmpty(topicConfigs) ? Collections.emptyList() : topicConfigs.stream()
                .map(this::buildTopicConfigRemotingEntity).filter(Objects::nonNull).collect(Collectors.toList());

        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.UPLOAD_ROUTE.getCode(),
            DefaultResponse.RouteUploadRequest.builder().address(address).topics(topics).build());
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

        return new PartitionConfigRemotingEntity(partitionConfig.getPartitionId(), RemotingUtil.getLocalAddress());
    }
}
