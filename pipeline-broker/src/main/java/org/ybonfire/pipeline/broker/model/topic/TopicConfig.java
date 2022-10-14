package org.ybonfire.pipeline.broker.model.topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public TopicConfig clone() {
        return new TopicConfig(this.topic,
            this.partitions.stream().map(PartitionConfig::clone).collect(Collectors.toList()));
    }
}
