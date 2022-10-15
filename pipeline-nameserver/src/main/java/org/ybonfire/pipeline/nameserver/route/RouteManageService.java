package org.ybonfire.pipeline.nameserver.route;

import org.ybonfire.pipeline.common.model.PartitionConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.request.nameserver.BrokerHeartbeatRequest;
import org.ybonfire.pipeline.nameserver.constant.NameServerConstant;
import org.ybonfire.pipeline.nameserver.model.BrokerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 路由管理服务
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:43
 */
public class RouteManageService {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final IRouteRepository routeRepository;

    public RouteManageService(final IRouteRepository routeRepository) {
        this.routeRepository = routeRepository;
        scheduledExecutorService.scheduleAtFixedRate(this::removeExpireRoute, 1000L, 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * @description: Broker上报路由数据
     * @param:
     * @return:
     * @date: 2022/07/04 17:53:21
     */
    public void uploadByBroker(final BrokerHeartbeatRequest request) {
        lock.writeLock().lock();
        try {
            routeRepository.updateBrokerData(buildBrokerData(request));
            routeRepository.updateRoute(buildTopicInfos(request));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @description: 查询所有Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 15:53:15
     */
    public List<BrokerData> selectAllBrokerData() {
        lock.readLock().lock();
        try {
            return routeRepository.selectAllBrokerData();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @description: 查询指定BrokerId的Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 16:03:52
     */
    public Optional<BrokerData> selectBrokerDataById(final String brokerId) {
        lock.readLock().lock();
        try {
            return routeRepository.selectBrokerDataById(brokerId);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @description: 查询所有路由信息
     * @param:
     * @return:
     * @date: 2022/07/11 13:52:32
     */
    public List<TopicInfo> selectAllTopicInfo() {
        lock.readLock().lock();
        try {
            return routeRepository.selectAllTopicInfo();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @description: 查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/07/11 14:02:51
     */
    public Optional<TopicInfo> selectTopicInfoByName(final String topic) {
        lock.readLock().lock();
        try {
            return routeRepository.selectTopicInfoByName(topic);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @description: 移除过期路由信息
     * @param:
     * @return:
     * @date: 2022/07/11 14:02:51
     */
    public void removeExpireRoute() {
        lock.writeLock().lock();
        try {
            this.routeRepository.removeExpireRoute(NameServerConstant.ROUTE_INFO_TTL_MILLIS);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @description: 构造BrokerData
     * @param:
     * @return:
     * @date: 2022/10/15 15:57:11
     */
    private BrokerData buildBrokerData(final BrokerHeartbeatRequest request) {
        if (request == null) {
            return null;
        }

        final String brokerId = request.getBrokerId();
        final Integer role = request.getRole();
        final String address = request.getAddress();

        return BrokerData.builder().brokerId(brokerId).role(role).address(address).build();
    }

    /**
     * @description: 构造路由信息
     * @param:
     * @return:
     * @date: 2022/07/09 14:47:44
     */
    private List<TopicInfo> buildTopicInfos(final BrokerHeartbeatRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }

        final String address = request.getAddress();
        final List<TopicConfigRemotingEntity> topics = request.getTopicConfigs();
        final List<TopicInfo> results = new ArrayList<>(request.getTopicConfigs().size());
        for (final TopicConfigRemotingEntity topic : topics) {
            final String topicName = topic.getTopic();
            final List<PartitionInfo> partitionInfos = new ArrayList<>(topic.getPartitions().size());
            for (final PartitionConfigRemotingEntity partition : topic.getPartitions()) {
                partitionInfos
                    .add(PartitionInfo.builder().partitionId(partition.getPartitionId()).address(address).build());
            }

            results.add(TopicInfo.builder().topic(topicName).partitions(partitionInfos).build());
        }

        return results;
    }
}
