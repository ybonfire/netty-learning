package org.ybonfire.pipeline.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.model.Message;

/**
 * 消息生产结果响应
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 16:30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProduceResultResponse {
    private Message message;
    private String topic;
    private Integer partitionId;
    private Long offset;
    private Boolean isSuccess;
}
