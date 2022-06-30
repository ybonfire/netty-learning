package org.ybonfire.pipeline.producer.callback;

import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * 消息投递回调函数接口
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 10:17
 */
public interface IMessageProduceCallback {
    
    /**
     * @description: 消息投递完成回调
     * @param:
     * @return:
     * @date: 2022/06/30 18:17:49
     */
    void onComplete(final ProduceResult result);
}
