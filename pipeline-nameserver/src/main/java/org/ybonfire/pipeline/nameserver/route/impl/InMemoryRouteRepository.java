package org.ybonfire.pipeline.nameserver.route.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.nameserver.model.TopicInfoWrapper;
import org.ybonfire.pipeline.nameserver.route.IRouteRepository;

/**
 * 内存路由信息存储器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:43
 */
public final class InMemoryRouteRepository implements IRouteRepository {
    private static final InMemoryRouteRepository INSTANCE = new InMemoryRouteRepository();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String/*topic*/, TopicInfoWrapper> topicInfoTable = new ConcurrentHashMap<>();

    private InMemoryRouteRepository() {}

    /**
     * @description: 更新路由信息
     * @param:
     * @return:
     * @date: 2022/07/01 18:08:35
     */
    @Override
    public void updateRoute(final List<TopicInfo> topicInfos) {
        if (topicInfos == null || topicInfos.isEmpty()) {
            return;
        }

        lock.writeLock().lock();
        try {
            for (final TopicInfo topicInfo : topicInfos) {
                final String topicName = topicInfo.getTopic();
                final TopicInfoWrapper prev = topicInfoTable.putIfAbsent(topicName, TopicInfoWrapper.wrap(topicInfo));
                if (prev != null) {
                    prev.merge(topicInfo);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @description: 查询所有路由信息
     * @param:
     * @return:
     * @date: 2022/07/10 09:45:54
     */
    @Override
    public List<TopicInfo> selectAll() {
        lock.readLock().lock();

        try {
            return topicInfoTable.values().stream().map(TopicInfoWrapper::getTopicInfo).collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @description: 查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/07/10 09:46:28
     */
    @Override
    public Optional<TopicInfo> selectByTopicName(final String topicName) {
        lock.readLock().lock();

        try {
            return Optional.ofNullable(topicInfoTable.get(topicName)).map(TopicInfoWrapper::getTopicInfo);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @description: 移除过期路由信息
     * @param:
     * @return:
     * @date: 2022/07/01 18:16:31
     */
    @Override
    public void removeExpireRoute(final long liveMillis) {
        lock.writeLock().lock();
        try {
            final Iterator<Map.Entry<String, TopicInfoWrapper>> iter = topicInfoTable.entrySet().iterator();
            while (iter.hasNext()) {
                final TopicInfoWrapper topicInfoWrapper = iter.next().getValue();
                if (isRouteExpired(topicInfoWrapper, liveMillis)) {
                    iter.remove();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @description: 判断路由信息是否过期
     * @param:
     * @return:
     * @date: 2022/07/01 18:16:22
     */
    private boolean isRouteExpired(final TopicInfoWrapper topicInfo, final long liveMillis) {
        return topicInfo.getLastUploadTimestamp() + liveMillis < System.currentTimeMillis();
    }

    /**
     * 获取InMemoryRouteRepository实例
     *
     * @return {@link InMemoryRouteRepository}
     */
    public static InMemoryRouteRepository getInstance() {
        return INSTANCE;
    }
}
