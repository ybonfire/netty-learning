package org.ybonfire.pipeline.producer.converter;

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
 * TopicInfo参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 09:42
 */
public final class TopicInfoConverter implements IConverter<TopicConfigRemotingEntity, TopicInfo> {
    private static final TopicInfoConverter INSTANCE = new TopicInfoConverter();

    public static TopicInfoConverter getInstance() {
        return INSTANCE;
    }

    private TopicInfoConverter() {}

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/07/28 20:31:40
     */
    @Override
    public TopicInfo convert(final TopicConfigRemotingEntity src) {
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
}
