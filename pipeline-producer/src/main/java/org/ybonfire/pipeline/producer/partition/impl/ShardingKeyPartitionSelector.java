package org.ybonfire.pipeline.producer.partition.impl;

import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.partition.AbstractPartitionSelector;
import org.ybonfire.pipeline.producer.route.RouteManager;

/**
 * ShardingKey策略Partition选择器
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 22:00
 */
public final class ShardingKeyPartitionSelector extends AbstractPartitionSelector {
    private final RoundRobinPartitionSelector roundRobinPartitionSelector;

    public ShardingKeyPartitionSelector(final RouteManager routeManager) {
        super(routeManager);
        this.roundRobinPartitionSelector = new RoundRobinPartitionSelector(routeManager);
    }

    /**
     * @description: 选择Partition
     * @param:
     * @return:
     * @date: 2022/06/29 16:09:00
     */
    @Override
    protected PartitionInfo doSelect(final Message message, final TopicInfo topicInfo) {
        final String key = message.getKey();
        if (key == null) {
            return this.roundRobinPartitionSelector.doSelect(message, topicInfo);
        }

        final int index = index(topicInfo, key);
        return topicInfo.getPartitions().get(index);
    }

    /**
     * @description: 计算PartitionIndex
     * @param:
     * @return:
     * @date: 2022/06/27 21:56:58
     */
    private int index(final TopicInfo topic, final String key) {
        return key.hashCode() % topic.getPartitions().size();
    }
}
