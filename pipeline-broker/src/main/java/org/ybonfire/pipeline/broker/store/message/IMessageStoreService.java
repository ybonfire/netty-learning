package org.ybonfire.pipeline.broker.store.message;

import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;
import org.ybonfire.pipeline.common.model.Message;

/**
 * 消息存储服务接口
 *
 * @author yuanbo
 * @date 2022-09-14 18:29
 */
public interface IMessageStoreService extends ILifeCycle {

    /**
     * @description: 存储消息
     * @param:
     * @return:
     * @date: 2022/09/14 18:30:19
     */
    void store(final String topic, final int partitionId, final Message message);

    /**
     * @description: 重新加载文件数据
     * @param:
     * @return:
     * @date: 2022/10/11 16:38:05
     */
    void reload();
}
