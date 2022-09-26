package org.ybonfire.pipeline.producer.client;

import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * Broker远程调用客户端接口
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 16:28
 */
public interface IBrokerClient {

    /**
     * @description: 投递消息
     * @param:
     * @return:
     * @date: 2022/06/30 10:44:03
     */
    ProduceResult produce(final MessageWrapper message, final String address);
}
