package org.ybonfire.pipeline.nameserver.converter;

import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.model.TopicInfoRemotingEntity;

/**
 * TopicInfo参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 09:42
 */
public final class TopicInfoConverter implements IConverter<TopicInfo, TopicInfoRemotingEntity> {
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
    public TopicInfoRemotingEntity convert(final TopicInfo src) {
        return null;
    }
}
