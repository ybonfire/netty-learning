package org.ybonfire.pipeline.broker.converter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.ybonfire.pipeline.broker.model.topic.PartitionConfig;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.PartitionConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;

/**
 * TopicConfigConverter
 *
 * @author yuanbo
 * @date 2022-09-23 11:25
 */
public final class TopicConfigConverter implements IConverter<TopicConfig, TopicConfigRemotingEntity> {
    private static final TopicConfigConverter INSTANCE = new TopicConfigConverter();

    public static TopicConfigConverter getInstance() {
        return INSTANCE;
    }

    private TopicConfigConverter() {}

    /**
     * @description: TopicConfig -> TopicConfigRemotingEntity
     * @param:
     * @return:
     * @date: 2022/09/23 11:25:48
     */
    @Override
    public TopicConfigRemotingEntity convert(final TopicConfig src) {
        if (src == null) {
            return null;
        }

        final String topic = src.getTopic();
        final List<PartitionConfigRemotingEntity> partitions = CollectionUtils.emptyIfNull(src.getPartitions()).stream()
            .map(this::convert).filter(Objects::nonNull).collect(Collectors.toList());

        return new TopicConfigRemotingEntity(topic, partitions);
    }

    /**
     * @description: partition -> PartitionConfigRemotingEntity
     * @param:
     * @return:
     * @date: 2022/10/20 23:52:30
     */
    private PartitionConfigRemotingEntity convert(final PartitionConfig partition) {
        if (partition == null) {
            return null;
        }

        return new PartitionConfigRemotingEntity(partition.getPartitionId());
    }
}
