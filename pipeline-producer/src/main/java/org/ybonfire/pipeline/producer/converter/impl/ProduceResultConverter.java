package org.ybonfire.pipeline.producer.converter.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.ybonfire.pipeline.common.protocol.ProduceResultRemotingEntity;
import org.ybonfire.pipeline.producer.converter.IConverter;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * ProduceResult参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 16:32
 */
public final class ProduceResultConverter implements IConverter<ProduceResultRemotingEntity, ProduceResult> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/06/30 16:37:05
     */
    @Override
    public ProduceResult convert(final ProduceResultRemotingEntity src) {
        if (src == null) {
            return null;
        }

        final String topic = src.getTopic();
        final int partitionId = src.getPartitionId() == null ? -1 : src.getPartitionId();
        final long offset = src.getOffset() == null ? -1L : src.getOffset();
        final boolean isSuccess = BooleanUtils.toBooleanDefaultIfNull(src.getIsSuccess(), false);

        return ProduceResult.builder().topic(topic).partitionId(partitionId).offset(offset).isSuccess(isSuccess)
            .build();
    }
}
