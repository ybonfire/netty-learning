package org.ybonfire.pipeline.broker.server;

import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.broker.config.BrokerConfig;
import org.ybonfire.pipeline.broker.handler.provider.BrokerRequestHandlerProvider;
import org.ybonfire.pipeline.broker.model.Role;
import org.ybonfire.pipeline.broker.role.RoleManager;
import org.ybonfire.pipeline.broker.util.ThreadPoolUtil;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.server.NettyRemotingServer;

/**
 * Broker服务
 *
 * @author Bo.Yuan5
 * @date 2022-08-24 21:36
 */
public final class Broker extends NettyRemotingServer {

    public Broker(final BrokerConfig config) {
        super(config);
        RoleManager.set(Role.of(config.getRole()));
    }

    @Override
    protected void registerRequestHandlers() {
        // ProduceMessageRequestHandler
        registerProduceMessageRequestHandler();
        // ConsumeMessageRequestHandler
        registerConsumeMessageRequestHandler();
    }

    /**
     * 注册ProduceMessage请求处理器
     */
    private void registerProduceMessageRequestHandler() {
        final ExecutorService produceMessageRequestHandleExecutor =
            ThreadPoolUtil.getProduceMessageHandlerExecutorService();
        registerHandler(RequestEnum.PRODUCE_MESSAGE.getCode(),
            BrokerRequestHandlerProvider.getProduceMessageRequestHandler(), produceMessageRequestHandleExecutor);
    }

    /**
     * 注册ConsumeMessage请求处理器
     */
    private void registerConsumeMessageRequestHandler() {
        final ExecutorService consumeMessageRequestHandleExecutor =
            ThreadPoolUtil.getConsumeMessageHandlerExecutorService();
    }
}
