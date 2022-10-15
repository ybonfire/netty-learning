package org.ybonfire.pipeline.nameserver.route;

import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.nameserver.model.BrokerData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 路由信息存储接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:42
 */
public interface IRouteRepository {

    /**
     * @description: 更新Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 15:51:39
     */
    void updateBrokerData(final BrokerData brokerData);

    /**
     * @description: 查询指定BrokerId的Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 15:53:26
     */
    Optional<BrokerData> selectBrokerDataById(final String brokerId);

    /**
     * @description: 查询所有Broker信息
     * @param:
     * @return:
     * @date: 2022/10/15 15:53:15
     */
    List<BrokerData> selectAllBrokerData();

    /**
     * @description: 更新路由信息
     * @param:
     * @return:
     * @date: 2022/07/01 18:08:35
     */
    void updateRoute(final List<TopicInfo> topicInfos);

    /**
     * @description: 查询所有路由信息
     * @param:
     * @return:
     * @date: 2022/07/10 09:45:54
     */
    List<TopicInfo> selectAllTopicInfo();

    /**
     * @description: 查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/07/10 09:46:28
     */
    Optional<TopicInfo> selectTopicInfoByName(final String topic);

    /**
     * @description: 批量查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/10/14 17:00:20
     */
    Map<String, TopicInfo> selectTopicInfoByNames(final String... topics);

    /**
     * @description: 移除过期路由信息
     * @param:
     * @return:
     * @date: 2022/07/01 18:16:31
     */
    void removeExpireRoute(final long liveMillis);
}
