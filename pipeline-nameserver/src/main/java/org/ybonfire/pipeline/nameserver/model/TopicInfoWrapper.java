package org.ybonfire.pipeline.nameserver.model;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicInfo;

import lombok.Getter;

/**
 * TopicInfo包装类
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 18:09
 */
@Getter
public class TopicInfoWrapper {
    private final TopicInfo topicInfo;
    private volatile long lastUploadTimestamp;

    private TopicInfoWrapper(final TopicInfo topicInfo) {
        this.topicInfo = topicInfo;
        this.lastUploadTimestamp = System.currentTimeMillis();
    }

    /**
     * @description: 融合TopicInfo
     * @param:
     * @return:
     * @date: 2022/07/09 16:00:56
     */
    public void merge(final TopicInfo anOther) {
        if (anOther == null || !StringUtils.equals(anOther.getTopic(), this.topicInfo.getTopic())) {
            return;
        }

        final Map<Integer, PartitionInfo> partitionGroupByPartitionId = this.topicInfo.getPartitions().stream()
            .collect(Collectors.toMap(PartitionInfo::getPartitionId, partitionInfo -> partitionInfo));
        for (final PartitionInfo partition : anOther.getPartitions()) {
            final int partitionId = partition.getPartitionId();
            partitionGroupByPartitionId.put(partitionId, partition);
        }

        this.lastUploadTimestamp = System.currentTimeMillis();
    }

    public static TopicInfoWrapper wrap(final TopicInfo topicInfo) {
        return new TopicInfoWrapper(topicInfo);
    }
}
