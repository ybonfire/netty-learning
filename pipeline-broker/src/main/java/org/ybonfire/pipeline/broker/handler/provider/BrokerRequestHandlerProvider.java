package org.ybonfire.pipeline.broker.handler.provider;

import org.ybonfire.pipeline.broker.handler.ProduceMessageRequestHandler;
import org.ybonfire.pipeline.broker.store.message.impl.DefaultMessageStoreService;
import org.ybonfire.pipeline.broker.topic.provider.TopicManagerProvider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Broker请求处理器Provider
 *
 * @author yuanbo
 * @date 2022-09-20 17:52
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BrokerRequestHandlerProvider {
    private static final ProduceMessageRequestHandler produceMessageRequestHandler =
        new ProduceMessageRequestHandler(TopicManagerProvider.getInstance(), new DefaultMessageStoreService());

    public static ProduceMessageRequestHandler getProduceMessageRequestHandler() {
        return produceMessageRequestHandler;
    }
}
