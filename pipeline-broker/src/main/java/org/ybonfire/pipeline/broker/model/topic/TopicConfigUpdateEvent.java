package org.ybonfire.pipeline.broker.model.topic;

import lombok.Builder;
import lombok.Data;

/**
 * TopicConfig更新事件
 *
 * @author yuanbo
 * @date 2022-10-14 13:53
 */
@Builder
@Data
public final class TopicConfigUpdateEvent {
    /**
     * 事件类型
     */
    private final TopicConfigUpdateTypeEnum type;
    /**
     * Topic名称
     */
    private final String topic;
    /**
     * 当前TopicConfig
     */
    private final TopicConfig config;
}
