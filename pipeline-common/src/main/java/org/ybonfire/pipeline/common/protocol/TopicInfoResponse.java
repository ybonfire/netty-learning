package org.ybonfire.pipeline.common.protocol;

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
public final class TopicInfoResponse {
    private String topic;
    private List<PartitionInfoResponse> partitions;
}
