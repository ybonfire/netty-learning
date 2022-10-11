package org.ybonfire.pipeline.nameserver.converter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.PartitionConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicInfo;

/**
 * TopicConfigRemotingEntityConverter
 *
 * @author yuanbo
 * @date 2022-09-26 14:04
 */
public class TopicInfoConverter implements IConverter<TopicInfo, TopicConfigRemotingEntity> {
    private static final TopicInfoConverter INSTANCE = new TopicInfoConverter();

    public static TopicInfoConverter getInstance() {
        return INSTANCE;
    }

    private TopicInfoConverter() {}

    /**
     * @description: 参数转换 TopicInfo -> TopicConfigRemotingEntity
     * @param:
     * @return:
     * @date: 2022/09/23 10:24:10
     */
    @Override
    public TopicConfigRemotingEntity convert(final TopicInfo src) {
        if (src == null) {
            return null;
        }

        final String topic = src.getTopic();
        final List<PartitionConfigRemotingEntity> partitions = src.getPartitions() == null ? Collections.emptyList()
            : src.getPartitions().stream().map(this::convert).filter(Objects::nonNull).collect(Collectors.toList());

        return new TopicConfigRemotingEntity(topic, partitions);
    }

    /**
     * @description: 参数转换 TopicConfigRemotingEntity -> TopicInfo
     * @param:
     * @return:
     * @date: 2022/07/28 20:31:40
     */
    @Override
    public TopicInfo revert(final TopicConfigRemotingEntity src) {
        if (src == null) {
            return null;
        }

        final String topic = src.getTopic();
        final List<PartitionInfo> partitions = src.getPartitions() == null ? Collections.emptyList()
            : src.getPartitions().stream().map(this::convert).filter(Objects::nonNull).collect(Collectors.toList());

        return TopicInfo.builder().topic(topic).partitions(partitions).build();
    }

    /**
     * @description: 参数转换 PartitionConfigRemotingEntity -> PartitionInfo
     * @param:
     * @return:
     * @date: 2022/09/23 10:24:10
     */
    private PartitionInfo convert(final PartitionConfigRemotingEntity src) {
        if (src == null) {
            return null;
        }

        return PartitionInfo.builder().partitionId(src.getPartitionId()).address(src.getAddress()).build();
    }

    /**
     * @description: 参数转换 PartitionInfo -> PartitionConfigRemotingEntity
     * @param:
     * @return:
     * @date: 2022/09/23 10:24:10
     */
    private PartitionConfigRemotingEntity convert(final PartitionInfo src) {
        if (src == null) {
            return null;
        }

        return new PartitionConfigRemotingEntity(src.getPartitionId(), src.getAddress());
    }
}
