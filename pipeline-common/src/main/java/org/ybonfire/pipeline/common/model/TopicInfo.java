package org.ybonfire.pipeline.common.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Topic信息
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:39
 */
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class TopicInfo {
    private String topic;
    private List<PartitionInfo> partitions;
}
