package org.ybonfire.pipeline.common.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

/**
 * Partition信息
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:40
 */
@Builder
@Getter
public class PartitionInfo {
    private final int partitionId;
    private final List<Node> nodes;

    /**
     * @description: 尝试获取PartitionLeader的Node信息
     * @param:
     * @return:
     * @date: 2022/06/27 22:02:53
     */
    public Optional<Node> tryToFindPartitionLeaderNode() {
        return nodes.stream().filter(node -> node.getRole() == NodeRole.LEADER).findAny();
    }

}
