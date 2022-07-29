package org.ybonfire.pipeline.nameserver.converter;

import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.PartitionInfoRemotingEntity;

/**
 * PartitionInfo参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 09:44
 */
public final class PartitionConverter implements IConverter<PartitionInfo, PartitionInfoRemotingEntity> {
    private final NodeConverter nodeConverter;

    public PartitionConverter(final NodeConverter nodeConverter) {
        this.nodeConverter = nodeConverter;
    }

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/07/28 20:31:40
     */
    @Override
    public PartitionInfoRemotingEntity convert(final PartitionInfo src) {
        return null;
    }
}
