package org.ybonfire.pipeline.broker.client;

import org.ybonfire.pipeline.broker.model.topic.TopicConfig;

import java.util.List;

/**
 * Nameserver远程调用客户端接口
 *
 * @author yuanbo
 * @date 2022-09-23 10:51
 */
public interface INameServerClient {

    /**
     * 上报主题配置
     *
     * @param topicConfigs 主题配置
     */
    void uploadTopicConfig(final List<TopicConfig> topicConfigs, final String address, final long timeoutMillis);
}
