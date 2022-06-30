package org.ybonfire.pipeline.producer.route;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.metadata.NameServers;

/**
 * 路由管理器
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:47
 */
public final class RouteManager {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, TopicInfo> topicInfoTable = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NameServers nameServers;

    public RouteManager(final NameServers nameServers) {
        this.nameServers = nameServers;
        this.scheduler.scheduleAtFixedRate(this::updateRouteInfo, 15 * 1000L, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * @description: 根据topic名称查询Topic信息
     * @param:
     * @return:
     * @date: 2022/06/27 21:36:35
     */
    public Optional<TopicInfo> selectTopicInfo(final String topic) {
        lock.readLock().lock();
        try {
            final Optional<TopicInfo> topicInfoOptional = selectFromCache(topic);
            return topicInfoOptional.isPresent() ? topicInfoOptional : selectFromRemote(topic);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @description: 更新路由信息
     * @param:
     * @return:
     * @date: 2022/06/29 09:54:32
     */
    private void updateRouteInfo() {
        // TODO 远程调用Nameserver异常处理
        final List<TopicInfo> result = nameServers.selectAllTopicInfo();
        lock.writeLock().lock();
        try {
            topicInfoTable.clear();
            topicInfoTable
                .putAll(result.stream().collect(Collectors.toMap(TopicInfo::getTopic, topicInfo -> topicInfo)));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @description: 从缓存路由表中查询指定Topic的路由信息
     * @param:
     * @return:
     * @date: 2022/06/29 09:53:26
     */
    private Optional<TopicInfo> selectFromCache(final String topic) {
        return Optional.ofNullable(topicInfoTable.get(topic));
    }

    /**
     * @description: 从NameServer查询指定Topic的路由信息
     * @param:
     * @return:
     * @date: 2022/06/29 09:53:37
     */
    private Optional<TopicInfo> selectFromRemote(final String topic) {
        // TODO 远程调用Nameserver异常处理
        return nameServers.selectTopicInfo(topic);
    }
}
