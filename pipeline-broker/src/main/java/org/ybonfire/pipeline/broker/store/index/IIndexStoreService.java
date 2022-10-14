package org.ybonfire.pipeline.broker.store.index;

import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;

import java.util.Optional;

/**
 * 消息索引存储服务接口
 *
 * @author yuanbo
 * @date 2022-10-07 10:42
 */
public interface IIndexStoreService extends ILifeCycle {

    /**
     * @description: 注册指定Topic、Partition的索引构建Worker
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:54
     */
    void register(final String topic, final int partitionId);

    /**
     * @description: 取消注册指定Topic、Partition的索引构建Worker
     * @param:
     * @return:
     * @date: 2022/10/13 18:12:18
     */
    void deregister(final String topic, final int partitionId);

    /**
     * @description: 尝试根据Topic、Partition获取索引文件
     * @param:
     * @return:
     * @date: 2022/10/13 17:33:50
     */
    Optional<IndexLog> tryToFindIndexLogByTopicPartition(final String topic, final int partitionId);

    /**
     * @description: 重新加载文件数据
     * @param:
     * @return:
     * @date: 2022/10/11 16:38:05
     */
    void reload();
}
