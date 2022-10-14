package org.ybonfire.pipeline.broker.model.store;

import java.nio.ByteBuffer;

import lombok.Builder;
import lombok.Data;

/**
 * 文件数据查询结果
 *
 * @author yuanbo
 * @date 2022-10-10 17:56
 */
@Builder
@Data
public class SelectMappedFileDataResult {
    private final int startPosition;
    private final int size;
    private final ByteBuffer data;
}
