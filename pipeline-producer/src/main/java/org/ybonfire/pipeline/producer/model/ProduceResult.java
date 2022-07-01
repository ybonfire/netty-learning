package org.ybonfire.pipeline.producer.model;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.model.Message;

/**
 * 消息生产结果
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 10:23
 */
@Builder
@Data
public class ProduceResult {
    private final String topic;
    private final int partitionId;
    private final long offset;
    private final boolean isSuccess;
    private Message message;
}
