package org.ybonfire.pipeline.nameserver.server;

import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.nameserver.processor.JoinClusterRequestProcessor;
import org.ybonfire.pipeline.nameserver.processor.SelectAllRouteRequestProcessor;
import org.ybonfire.pipeline.nameserver.processor.SelectByTopicNameRequestProcessor;
import org.ybonfire.pipeline.nameserver.processor.UploadRouteRequestProcessor;
import org.ybonfire.pipeline.nameserver.util.ThreadPoolUtil;
import org.ybonfire.pipeline.server.NettyRemotingServer;
import org.ybonfire.pipeline.server.config.NettyServerConfig;

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
        // RouteUploadRequestProcessor
        registerRouteUploadRequestProcessor();
        // RouteSelectAllRequestProcessor
        registerRouteSelectAllRequestProcessor();
        // RouteSelectRequestProcessor
        registerRouteSelectRequestProcessor();
        // JoinClusterRequestProcessor
        registerJoinClusterRequestProcessor();
    }

    /**
     * 注册上报路由请求处理器
     */
    private void registerRouteUploadRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.UPLOAD_ROUTE.getCode(), UploadRouteRequestProcessor.getInstance(),
            processorExecutor);
    }

    /**
     * 注册查询全部路由请求处理器
     */
    private void registerRouteSelectAllRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.SELECT_ALL_ROUTE.getCode(), SelectAllRouteRequestProcessor.getInstance(),
            processorExecutor);
    }

    /**
     * 注册路线查询请求处理器
     */
    private void registerRouteSelectRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.SELECT_ROUTE.getCode(), SelectByTopicNameRequestProcessor.getInstance(),
            processorExecutor);
    }

    /**
     * 注册加入集群请求处理器
     */
    private void registerJoinClusterRequestProcessor() {
        final ExecutorService processorExecutor = ThreadPoolUtil.getNameserverProcessorExecutorService();
        registerRequestProcessor(RequestEnum.JOIN_CLUSTER.getCode(), JoinClusterRequestProcessor.getInstance(),
            processorExecutor);
    }
}
