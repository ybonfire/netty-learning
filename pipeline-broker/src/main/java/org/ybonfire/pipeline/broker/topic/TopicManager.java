package org.ybonfire.pipeline.broker.topic;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.common.model.TopicInfo;

/**
 * Topic管理器
 *
 * @author yuanbo
 * @date 2022-09-14 15:34
 */
public class TopicManager {
    private static final Map<String, TopicInfo> TOPIC_INFO_TABLE = new ConcurrentHashMap<>();
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    /**
     * 查询主题信息
     *
     * @param topic 主题
     * @return {@link Optional}
     */
    public Optional<TopicInfo> selectTopicInfo(final String topic) {
        try {
            if (LOCK.readLock().tryLock(BrokerConstant.TOPIC_INFO_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    return Optional.ofNullable(TOPIC_INFO_TABLE.get(topic));
                } finally {
                    LOCK.readLock().unlock();
                }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // ignore
        }

        return Optional.empty();
    }
}
