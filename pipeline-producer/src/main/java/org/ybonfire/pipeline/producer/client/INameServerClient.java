package org.ybonfire.pipeline.producer.client;

import org.ybonfire.pipeline.common.model.TopicInfo;

import java.util.List;
import java.util.Optional;

/**
 * Nameserver远程调用客户端接口
 *
 * @author Bo.Yuan5
 * @date 2022-08-04 17:53
 */
public interface INameServerClient {

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
}
