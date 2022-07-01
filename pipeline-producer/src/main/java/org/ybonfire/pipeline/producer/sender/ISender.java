package org.ybonfire.pipeline.producer.sender;

import org.ybonfire.pipeline.producer.model.MessageWrapper;

/**
 * 发送器接口
 *
 * @author Bo.Yuan5
 * @date 2022-06-28 09:56
 */
public interface ISender {

    /**
     * @description: 启动发送器
     * @param:
     * @return:
     * @date: 2022/07/01 13:28:54
     */
    void start();

    /**
     * @description: 关闭发送器
     * @param:
     * @return:
     * @date: 2022/07/01 13:29:06
     */
    void stop();

    /**
     * @description: 发送消息
     * @param:
     * @return:
     * @date: 2022/06/28 13:45:14
     */
    void send(final MessageWrapper wrapper);
}
