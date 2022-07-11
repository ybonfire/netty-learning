package org.ybonfire.pipeline.producer.partition.impl;

import java.util.concurrent.ThreadLocalRandom;

import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.partition.AbstractPartitionSelector;
import org.ybonfire.pipeline.producer.route.RouteManager;

/**
 * 随机策略Partition选择器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 18:07
 */
public final class RandomPartitionSelector extends AbstractPartitionSelector {

    public RandomPartitionSelector(final RouteManager routeManager) {
        super(routeManager);
    }

    /**
     * @description: 选择Partition
     * @param:
     * @return:
     * @date: 2022/07/11 18:08:22
     */
    @Override
    protected PartitionInfo doSelect(final Message message, final TopicInfo topicInfo) {
        final int index = index(topicInfo);
        return topicInfo.getPartitions().get(index);
    }

    /**
     * @description: 计算PartitionIndex
     * @param:
     * @return:
     * @date: 2022/07/11 18:08:22
     */
    private int index(final TopicInfo topic) {
        return ThreadLocalRandom.current().nextInt() % topic.getPartitions().size();
    }
}
