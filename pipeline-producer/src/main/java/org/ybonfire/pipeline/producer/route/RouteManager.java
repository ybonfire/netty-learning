package org.ybonfire.pipeline.producer.route;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.metadata.NameServers;

/**
 * 路由管理器
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:47
 */
public final class RouteManager {
    private final IInternalLogger logger = new SimpleInternalLogger();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, TopicInfo> topicInfoTable = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NameServers nameServers;

    public RouteManager(final NameServers nameServers) {
        this.nameServers = nameServers;
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            nameServers.start();
            scheduler.scheduleAtFixedRate(this::updateRouteInfo, 0L, 15 * 1000L, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (started.compareAndSet(true, false)) {
            nameServers.stop();
            scheduler.shutdown();
        }
    }

    /**
     * @description: 根据topic名称查询Topic信息
     * @param:
     * @return:
     * @date: 2022/06/27 21:36:35
     */
    public Optional<TopicInfo> selectTopicInfo(final String topic, final long timeoutMillis) {
        lock.readLock().lock();
        try {
            final Optional<TopicInfo> topicInfoOptional = selectFromCache(topic);
            return topicInfoOptional.isPresent() ? topicInfoOptional : selectFromRemote(topic, timeoutMillis);
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
        try {
            final List<TopicInfo> result = nameServers.selectAllTopicInfo(10 * 1000L);
            lock.writeLock().lock();
            try {
                topicInfoTable.clear();
                topicInfoTable
                    .putAll(result.stream().collect(Collectors.toMap(TopicInfo::getTopic, topicInfo -> topicInfo)));
            } finally {
                lock.writeLock().unlock();
            }
        } catch (Exception ex) {
            logger.warn("远程调用NameServer查询路由信息异常");
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
    private Optional<TopicInfo> selectFromRemote(final String topic, final long timeoutMillis) {
        try {
            return nameServers.selectTopicInfo(topic, timeoutMillis);
        } catch (Exception ex) {
            logger.warn("远程调用NameServer查询路由信息异常");
            return Optional.empty();
        }
    }
}
