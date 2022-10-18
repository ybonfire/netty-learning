package org.ybonfire.pipeline.broker.model.heartbeat;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.broker.model.RoleEnum;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;

import java.util.List;

/**
 * 心跳数据
 *
 * @author yuanbo
 * @date 2022-10-15 13:41
 */
@Builder
@Data
public class HeartbeatData {
    /**
     * BrokerId
     */
    private final String brokerId;
    /**
     * Broker角色
     */
    private final RoleEnum role;
    /**
     * Broker地址
     */
    private final String address;
    /**
     * Topic信息
     */
    private final List<TopicConfig> topicConfigs;
    /**
     * 是否允许自动创建Topic
     */
    private final boolean enableAutoCreateTopic;
    /**
     * 心跳时间戳
     */
    private final long timestamp = System.currentTimeMillis();
}
