package org.ybonfire.pipeline.broker.topic;

import org.ybonfire.pipeline.broker.callback.ITopicConfigUpdateEventCallback;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Topic管理器接口
 *
 * @author yuanbo
 * @date 2022-10-14 14:21
 */
public interface ITopicConfigManager extends ILifeCycle {

    /**
     * @description: 添加Topic配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:25:33
     */
    void addTopicConfig(final TopicConfig config);

    /**
     * @description: 更新Topic配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:25:45
     */
    void updateTopicConfig(final TopicConfig config);

    /**
     * @description: 删除Topic配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:25:52
     */
    void deleteTopicConfig(final String topic);

    /**
     * @description: 查询所有Topic配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:26:21
     */
    List<TopicConfig> selectAllTopicConfigs();

    /**
     * @description: 查询指定名称的Topic配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:26:59
     */
    Optional<TopicConfig> selectTopicConfig(final String topic);

    /**
     * @description: 查询指定名称的Topic配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:27:57
     */
    Map<String, TopicConfig> selectTopicConfig(final String... topics);

    /**
     * @description: 持久化主题配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:32:43
     */
    void persist();

    /**
     * @description: 加载持久化的主题配置
     * @param:
     * @return:
     * @date: 2022/10/14 14:32:45
     */
    void reload();

    /**
     * @description: 注册TopicConfig更新事件回调
     * @param:
     * @return:
     * @date: 2022/10/14 14:34:19
     */
    void register(final ITopicConfigUpdateEventCallback callback);

    /**
     * @description: 取消注册TopicConfig更新事件回调
     * @param:
     * @return:
     * @date: 2022/10/14 14:34:19
     */
    void deregister(final ITopicConfigUpdateEventCallback callback);
}
