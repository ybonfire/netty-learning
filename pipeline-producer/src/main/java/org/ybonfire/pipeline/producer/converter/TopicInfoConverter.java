package org.ybonfire.pipeline.producer.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.model.TopicInfoRemotingEntity;

/**
 * TopicInfo参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 09:42
 */
public final class TopicInfoConverter implements IConverter<TopicInfoRemotingEntity, TopicInfo> {
    private final PartitionConverter partitionConverter;

    public TopicInfoConverter(final PartitionConverter partitionConverter) {
        this.partitionConverter = partitionConverter;
    }

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/07/28 20:31:40
     */
    @Override
    public TopicInfo convert(TopicInfoRemotingEntity src) {
        if (src == null) {
            return null;
        }

        final String topic = src.getTopic();
        final List<PartitionInfo> partitions = src.getPartitions() == null ? Collections.emptyList()
            : src.getPartitions().stream().map(partitionConverter::convert).collect(Collectors.toList());

        return TopicInfo.builder().topic(topic).partitions(partitions).build();
    }
}
