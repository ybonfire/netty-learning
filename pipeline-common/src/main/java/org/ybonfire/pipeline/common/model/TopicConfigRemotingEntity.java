package org.ybonfire.pipeline.common.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Topic信息响应体
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:39
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class TopicConfigRemotingEntity {
    private String topic;
    private List<PartitionConfigRemotingEntity> partitions;
}
