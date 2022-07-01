package org.ybonfire.pipeline.producer.partition;

import java.util.Optional;

import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.route.RouteManager;

/**
 * Partition选择器
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 10:01
 */
public abstract class AbstractPartitionSelector implements IPartitionSelector {
    private final RouteManager routeManager;

    protected AbstractPartitionSelector(final RouteManager routeManager) {
        this.routeManager = routeManager;
    }

    /**
     * @description: 根据消息选择需要投递的目标PartitionId
     * @param:
     * @return:
     * @date: 2022/06/29 16:04:16
     */
    @Override
    public Optional<PartitionInfo> select(final Message message, final long timeoutMillis) {
        final Optional<TopicInfo> topicInfoOptional = tryToFindTopicInfo(message.getTopic(), timeoutMillis);
        return topicInfoOptional.map(topicInfo -> doSelect(message, topicInfo));
    }

    /**
     * @description: 尝试获取TopicInfo
     * @param:
     * @return:
     * @date: 2022/06/29 16:00:57
     */
    private Optional<TopicInfo> tryToFindTopicInfo(final String topic, final long timeoutMillis) {
        return routeManager.selectTopicInfo(topic, timeoutMillis);
    }

    /**
     * @description: 选择Partition
     * @param:
     * @return:
     * @date: 2022/06/29 16:04:07
     */
    protected abstract PartitionInfo doSelect(final Message message, final TopicInfo topicInfo);
}
