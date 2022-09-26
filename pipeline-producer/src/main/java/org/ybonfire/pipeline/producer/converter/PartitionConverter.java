package org.ybonfire.pipeline.producer.converter;

import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.PartitionConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.PartitionInfo;

/**
 * PartitionInfo参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 09:44
 */
public final class PartitionConverter implements IConverter<PartitionConfigRemotingEntity, PartitionInfo> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/07/28 20:31:40
     */
    @Override
    public PartitionInfo convert(final PartitionConfigRemotingEntity src) {
        if (src == null) {
            return null;
        }

        return PartitionInfo.builder().partitionId(src.getPartitionId()).build();
    }
}
