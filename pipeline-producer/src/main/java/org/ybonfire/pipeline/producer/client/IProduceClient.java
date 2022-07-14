package org.ybonfire.pipeline.producer.client;

import java.util.List;
import java.util.Optional;

import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;

/**
 * 远程调用客户端接口
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 16:28
 */
public interface IProduceClient {
    /**
     * @description: 发送查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:02
     */
    List<TopicInfo> selectAllTopicInfo(final String address, final long timeoutMillis);

    /**
     * @description: 发送查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:04
     */
    Optional<TopicInfo> selectTopicInfo(final String topic, final String address, final long timeoutMillis);

    /**
     * @description: 投递消息
     * @param:
     * @return:
     * @date: 2022/06/30 10:44:03
     */
    ProduceResult produce(final MessageWrapper message, final String address, final long timeoutMillis);
}
