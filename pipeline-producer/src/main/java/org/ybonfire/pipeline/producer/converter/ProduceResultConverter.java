package org.ybonfire.pipeline.producer.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.protocol.response.broker.SendMessageResponse;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * ProduceResult参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 16:32
 */
public final class ProduceResultConverter implements IConverter<SendMessageResponse, ProduceResult> {
    private static final ProduceResultConverter INSTANCE = new ProduceResultConverter();

    public static ProduceResultConverter getINSTANCE() {
        return INSTANCE;
    }

    private ProduceResultConverter() {}

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/06/30 16:37:05
     */
    @Override
    public ProduceResult convert(final SendMessageResponse src) {
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
