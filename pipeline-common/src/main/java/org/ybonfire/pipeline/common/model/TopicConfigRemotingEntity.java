package org.ybonfire.pipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    /**
     * topic名称
     */
    private String topic;
    private List<PartitionConfigRemotingEntity> partitions;
}
