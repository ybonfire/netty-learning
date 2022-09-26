package org.ybonfire.pipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Partition信息
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:40
 */
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartitionInfo {
    private int partitionId;
    private String address;
}
