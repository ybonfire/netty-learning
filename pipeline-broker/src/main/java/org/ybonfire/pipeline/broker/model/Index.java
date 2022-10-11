package org.ybonfire.pipeline.broker.model;

import lombok.Builder;
import lombok.Data;

/**
 * Message索引数据
 *
 * @author yuanbo
 * @date 2022-10-10 19:06
 */
@Builder
@Data
public final class Index {
    private final int startOffset;
    private final int size;
    private final long timestamp;
}
