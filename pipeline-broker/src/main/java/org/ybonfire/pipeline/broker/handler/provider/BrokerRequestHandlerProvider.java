package org.ybonfire.pipeline.broker.handler.provider;

import org.ybonfire.pipeline.broker.handler.ProduceMessageRequestHandler;

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
    private static final ProduceMessageRequestHandler HANDLER = new ProduceMessageRequestHandler();

    public static ProduceMessageRequestHandler getHandler() {
        return HANDLER;
    }
}
