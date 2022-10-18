package org.ybonfire.pipeline.producer.partition.impl;

import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.partition.AbstractPartitionSelector;
import org.ybonfire.pipeline.producer.route.RouteManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-Robin策略Partition选择器
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:28
 */
public final class RoundRobinPartitionSelector extends AbstractPartitionSelector {
    private final Map<TopicInfo, AtomicInteger> indexTable = new ConcurrentHashMap<>();

    public RoundRobinPartitionSelector(final RouteManager routeManager) {
        super(routeManager);
    }

    /**
     * @description: 选择Partition
     * @param:
     * @return:
     * @date: 2022/06/29 16:04:43
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
     * @date: 2022/06/27 21:56:58
     */
    private int index(final TopicInfo topic) {
        indexTable.computeIfAbsent(topic,
            value -> new AtomicInteger(ThreadLocalRandom.current().nextInt(topic.getPartitions().size())));
        return indexTable.get(topic).incrementAndGet() % topic.getPartitions().size();
    }
}
