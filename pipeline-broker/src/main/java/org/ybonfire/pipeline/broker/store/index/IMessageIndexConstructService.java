package org.ybonfire.pipeline.broker.store.index;

import org.ybonfire.pipeline.broker.store.message.MessageLog;

/**
 * 消息索引创建服务接口
 *
 * @author yuanbo
 * @date 2022-10-07 10:42
 */
public interface IMessageIndexConstructService {

    /**
     * @description: 启动消息索引构建服务
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:48
     */
    void start();

    /**
     * @description: 注册消息文件对象
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:54
     */
    void register(final MessageLog messageLog);

    /**
     * @description: 停止消息索引构建服务
     * @param:
     * @return:
     * @date: 2022/10/11 16:37:01
     */
    void stop();
}
