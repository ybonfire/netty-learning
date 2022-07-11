package org.ybonfire.pipeline.nameserver.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ybonfire.pipeline.common.model.Node;
import org.ybonfire.pipeline.common.model.NodeRole;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.PartitionInfoRemotingEntity;
import org.ybonfire.pipeline.common.protocol.RouteUploadRemotingEntity;
import org.ybonfire.pipeline.common.protocol.TopicInfoRemotingEntity;
import org.ybonfire.pipeline.nameserver.constant.NameServerConstant;

/**
 * 路由管理服务
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:43
 */
public class RouteManageService {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
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
    public void uploadByBroker(final RouteUploadRemotingEntity data) {
        routeRepository.updateRoute(buildTopicInfos(data));
    }

    /**
     * @description: 查询所有路由信息
     * @param:
     * @return:
     * @date: 2022/07/11 13:52:32
     */
    public List<TopicInfo> selectAll() {
        return routeRepository.selectAll();
    }

    /**
     * @description: 查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/07/11 14:02:51
     */
    public Optional<TopicInfo> selectByTopicName(final String topicName) {
        return routeRepository.selectByTopicName(topicName);
    }

    /**
     * @description: 移除过期路由信息
     * @param:
     * @return:
     * @date: 2022/07/11 14:02:51
     */
    public void removeExpireRoute() {
        this.routeRepository.removeExpireRoute(NameServerConstant.ROUTE_INFO_TTL_MILLIS);
    }

    /**
     * @description: 构造路由信息
     * @param:
     * @return:
     * @date: 2022/07/09 14:47:44
     */
    private List<TopicInfo> buildTopicInfos(final RouteUploadRemotingEntity data) {
        if (data == null) {
            return Collections.emptyList();
        }

        final Node node = Node.builder().address(data.getAddress()).role(NodeRole.of(data.getRole())).build();
        final List<TopicInfoRemotingEntity> topics = data.getTopics();
        final List<TopicInfo> results = new ArrayList<>(data.getTopics().size());
        for (final TopicInfoRemotingEntity topic : topics) {
            final String topicName = topic.getTopic();
            final List<PartitionInfo> partitionInfos = new ArrayList<>(topic.getPartitions().size());
            for (PartitionInfoRemotingEntity partition : topic.getPartitions()) {
                partitionInfos.add(PartitionInfo.builder().partitionId(partition.getPartitionId())
                    .nodes(Stream.of(node).collect(Collectors.toList())).build());
            }

            results.add(TopicInfo.builder().topic(topicName).partitions(partitionInfos).build());
        }

        return results;
    }
}
