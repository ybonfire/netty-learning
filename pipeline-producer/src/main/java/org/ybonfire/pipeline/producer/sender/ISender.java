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
     * @description: 发送消息
     * @param:
     * @return:
     * @date: 2022/06/28 13:45:14
     */
    void send(final MessageWrapper wrapper);
}
