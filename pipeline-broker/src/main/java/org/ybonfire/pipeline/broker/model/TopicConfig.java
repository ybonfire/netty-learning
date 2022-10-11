package org.ybonfire.pipeline.broker.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Topic配置信息
 *
 * @author yuanbo
 * @date 2022-09-22 14:56
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class TopicConfig {
    private String topic;
    private List<PartitionConfig> partitions;
}
