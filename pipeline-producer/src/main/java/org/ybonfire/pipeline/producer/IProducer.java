package org.ybonfire.pipeline.producer;

import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.producer.callback.IMessageProduceCallback;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * 生产者接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 13:26
 */
public interface IProducer extends ILifeCycle {

    /**
     * @description: 同步消息投递
     * @param:
     * @return:
     * @date: 2022/07/01 13:27:17
     */
    ProduceResult produce(final Message message, final long timeoutMillis);

    /**
     * @description: 异步消息投递
     * @param:
     * @return:
     * @date: 2022/07/15 10:07:05
     */
    void produce(final Message message, final IMessageProduceCallback callback, final long timeoutMillis);

    /**
     * @description: 单向消息投递
     * @param:
     * @return:
     * @date: 2022/07/15 10:07:21
     */
    void produce(final Message message);
}
