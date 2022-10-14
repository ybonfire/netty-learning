package org.ybonfire.pipeline.broker.model.topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Partition配置信息
 *
 * @author yuanbo
 * @date 2022-09-22 14:56
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartitionConfig {
    private int partitionId;

    @Override
    public PartitionConfig clone() {
        return new PartitionConfig(this.partitionId);
    }
}
