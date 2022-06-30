package org.ybonfire.pipeline.common.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * Topic信息
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:39
 */
@Builder
@Getter
public final class TopicInfo {
    private final String topic;
    private final List<PartitionInfo> partitions;
}
