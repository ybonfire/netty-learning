package org.ybonfire.pipeline.broker.model.message;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.model.Message;

import java.util.List;

/**
 * 消息查询结果
 *
 * @author yuanbo
 * @date 2022-10-20 23:37
 */
@Builder
@Data
public class SelectMessageResult {
    /**
     * topic名称
     */
    private final String topic;
    /**
     * partitionId
     */
    private final int partitionId;
    /**
     * 起始逻辑偏移量
     */
    private final int startLogicOffset;
    /**
     * 消息
     */
    private final List<Message> messages;
    /**
     * 结果类型
     */
    private final SelectMessageResultTypeEnum type;
}
