package org.ybonfire.pipeline.nameserver.route.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.nameserver.model.BrokerData;
import org.ybonfire.pipeline.nameserver.model.BrokerDataWrapper;
import org.ybonfire.pipeline.nameserver.model.TopicInfoWrapper;
import org.ybonfire.pipeline.nameserver.route.IRouteRepository;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存路由信息存储器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:43
 */
public final class InMemoryRouteRepository implements IRouteRepository {
    private static final InMemoryRouteRepository INSTANCE = new InMemoryRouteRepository();
    private final Map<String/*topic*/, TopicInfoWrapper> topicInfoTable = new ConcurrentHashMap<>();
    private final Map<String/*brokerId*/, BrokerDataWrapper> brokerDataTable = new ConcurrentHashMap<>();

    private InMemoryRouteRepository() {}

    /**
     * @description: 更新Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 15:39:31
     */
    @Override
    public void updateBrokerData(final BrokerData brokerData) {
        if (brokerData == null) {
            return;
        }

        brokerDataTable.put(brokerData.getBrokerId(), BrokerDataWrapper.wrap(brokerData));
    }

    /**
     * @description: 查询指定BrokerId的Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 15:53:26
     */
    @Override
    public Optional<BrokerData> selectBrokerDataById(final String brokerId) {
        if (brokerId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(brokerDataTable.get(brokerId)).map(BrokerDataWrapper::getBrokerData);
    }

    /**
     * @description: 查询所有Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 15:53:15
     */
    @Override
    public List<BrokerData> selectAllBrokerData() {
        return brokerDataTable.values().stream().map(BrokerDataWrapper::getBrokerData).collect(Collectors.toList());
    }

    /**
     * @description: 更新路由信息
     * @param:
     * @return:
     * @date: 2022/07/01 18:08:35
     */
    @Override
    public void updateRoute(final List<TopicInfo> topicInfos) {
        if (CollectionUtils.isEmpty(topicInfos)) {
            return;
        }

        for (final TopicInfo topicInfo : topicInfos) {
            final String topicName = topicInfo.getTopic();
            final TopicInfoWrapper prev = topicInfoTable.putIfAbsent(topicName, TopicInfoWrapper.wrap(topicInfo));
            if (prev != null) {
                prev.merge(topicInfo);
            }
        }
    }

    /**
     * @description: 查询所有路由信息
     * @param:
     * @return:
     * @date: 2022/07/10 09:45:54
     */
    @Override
    public List<TopicInfo> selectAllTopicInfo() {
        return topicInfoTable.values().stream().map(TopicInfoWrapper::getTopicInfo).collect(Collectors.toList());
    }

    /**
     * @description: 查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/07/10 09:46:28
     */
    @Override
    public Optional<TopicInfo> selectTopicInfoByName(final String topic) {
        return Optional.ofNullable(topicInfoTable.get(topic)).map(TopicInfoWrapper::getTopicInfo);
    }

    /**
     * @description: 批量查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/10/14 17:00:20
     */
    @Override
    public Map<String, TopicInfo> selectTopicInfoByNames(final String... topics) {
        return Arrays.stream(topics)
            .collect(Collectors.toMap(topic -> topic, topic -> topicInfoTable.get(topic).getTopicInfo()));
    }

    /**
     * @description: 移除过期路由信息
     * @param:
     * @return:
     * @date: 2022/07/01 18:16:31
     */
    @Override
    public void removeExpireRoute(final long liveMillis) {
        // 移除过期TopicInfo
        final Iterator<Map.Entry<String, TopicInfoWrapper>> topicInfoIter = topicInfoTable.entrySet().iterator();
        while (topicInfoIter.hasNext()) {
            final TopicInfoWrapper topicInfoWrapper = topicInfoIter.next().getValue();
            if (isRouteExpired(topicInfoWrapper, liveMillis)) {
                topicInfoIter.remove();
            }
        }
        // 移除过期BrokerData
        final Iterator<Map.Entry<String, BrokerDataWrapper>> brokerDataIter = brokerDataTable.entrySet().iterator();
        while (brokerDataIter.hasNext()) {
            final BrokerDataWrapper brokerDataWrapper = brokerDataIter.next().getValue();
            if (isBrokerExpired(brokerDataWrapper, liveMillis)) {
                brokerDataIter.remove();
            }
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
     * @description: 判断Broker信息是否过期
     * @param:
     * @return:
     * @date: 2022/07/01 18:16:22
     */
    private boolean isBrokerExpired(final BrokerDataWrapper brokerData, final long liveMillis) {
        return brokerData.getLastUploadTimestamp() + liveMillis < System.currentTimeMillis();
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
