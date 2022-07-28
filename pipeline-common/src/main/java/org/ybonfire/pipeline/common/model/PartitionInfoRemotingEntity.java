package org.ybonfire.pipeline.common.model;

import java.util.List;

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
public class PartitionInfoRemotingEntity {
    private Integer partitionId;
    private List<NodeRemotingEntity> nodes;
}
