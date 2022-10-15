package org.ybonfire.pipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Partition信息响应体
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartitionConfigRemotingEntity {
    private Integer partitionId;
}
