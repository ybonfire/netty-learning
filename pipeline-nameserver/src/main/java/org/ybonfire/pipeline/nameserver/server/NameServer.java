package org.ybonfire.pipeline.nameserver.server;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.nameserver.processor.BrokerHeartbeatRequestProcessor;
import org.ybonfire.pipeline.nameserver.processor.JoinClusterRequestProcessor;
import org.ybonfire.pipeline.nameserver.processor.SelectAllRouteRequestProcessor;
import org.ybonfire.pipeline.nameserver.processor.SelectByTopicNameRequestProcessor;
import org.ybonfire.pipeline.nameserver.processor.SelectByTopicNamesRequestProcessor;
import org.ybonfire.pipeline.nameserver.util.ThreadPoolUtil;
import org.ybonfire.pipeline.server.NettyRemotingServer;
import org.ybonfire.pipeline.server.config.NettyServerConfig;

import java.util.concurrent.ExecutorService;

/**
 * NameServer服务
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:13
 */
public final class NameServer extends NettyRemotingServer {

    public NameServer(final NettyServerConfig config) {
        super(config);
    }

    /**
     * @description: 注册NameServer请求处理器
     * @param:
     * @return:
     * @date: 2022/07/01 17:41:03
     */
    @Override
    protected void registerRequestProcessor() {
        // BrokerHeartbeatRequestProcessor
        registerBrokerHeartbeatRequestProcessor();
        // RouteSelectAllRequestProcessor
        registerRouteSelectAllRequestProcessor();
        // RouteSelectRequestProcessor
        registerRouteSelectRequestProcessor();
        // RoutesSelectRequestProcessor
        registerRoutesSelectRequestProcessor();
        // JoinClusterRequestProcessor
        registerJoinClusterRequestProcessor();
    }

    /**
     * 注册BrokerHeartbeatRequestProcessor
     */
    private void registerBrokerHeartbeatRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.BROKER_HEARTBEAT.getCode(), BrokerHeartbeatRequestProcessor.getInstance(),
            processorExecutor);
    }

    /**
     * 注册SelectAllRouteRequestProcessor
     */
    private void registerRouteSelectAllRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.SELECT_ALL_ROUTE.getCode(), SelectAllRouteRequestProcessor.getInstance(),
            processorExecutor);
    }

    /**
     * 注册SelectByTopicNameRequestProcessor
     */
    private void registerRouteSelectRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.SELECT_ROUTE.getCode(), SelectByTopicNameRequestProcessor.getInstance(),
            processorExecutor);
    }

    /**
     * 注册SelectByTopicNamesRequestProcessor
     */
    private void registerRoutesSelectRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.SELECT_ROUTES.getCode(), SelectByTopicNamesRequestProcessor.getInstance(),
            processorExecutor);
    }

    /**
     * 注册JoinClusterRequestProcessor
     */
    private void registerJoinClusterRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.JOIN_CLUSTER.getCode(), JoinClusterRequestProcessor.getInstance(),
            processorExecutor);
    }
}
