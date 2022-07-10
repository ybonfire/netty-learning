package org.ybonfire.pipeline.nameserver.route;

import org.ybonfire.pipeline.common.model.TopicInfo;

import java.util.List;
import java.util.Optional;

/**
 * 路由信息存储接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:42
 */
public interface IRouteRepository {

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
    List<TopicInfo> SelectAll();

    /**
     * @description: 查询指定名称的Topic信息
     * @param:
     * @return:
     * @date: 2022/07/10 09:46:28
     */
    Optional<TopicInfo> selectByTopicName(final String topicName);

    /**
     * @description: 移除过期路由信息
     * @param:
     * @return:
     * @date: 2022/07/01 18:16:31
     */
    void removeExpireRoute(final long liveMillis);
}
