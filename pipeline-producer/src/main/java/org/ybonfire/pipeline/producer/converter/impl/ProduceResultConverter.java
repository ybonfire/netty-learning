package org.ybonfire.pipeline.producer.converter.impl;

import org.ybonfire.pipeline.common.protocol.ProduceResultResponse;
import org.ybonfire.pipeline.producer.converter.IConverter;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * ProduceResult参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 16:32
 */
public final class ProduceResultConverter implements IConverter<ProduceResultResponse, ProduceResult> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/06/30 16:37:05
     */
    @Override
    public ProduceResult convert(final ProduceResultResponse src) {
        if (src == null) {
            return null;
        }

        final String topic = src.getTopic();
        final int partitionId = src.getPartitionId() == null ? -1 : src.getPartitionId();
        final long offset = src.getOffset() == null ? -1L : src.getOffset();
        final boolean isSuccess = partitionId == -1 || offset == -1L;

        return ProduceResult.builder().topic(topic).partitionId(partitionId).offset(offset).isSuccess(isSuccess)
            .build();
    }
}
