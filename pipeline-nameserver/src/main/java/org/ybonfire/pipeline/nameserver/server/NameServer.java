package org.ybonfire.pipeline.nameserver.server;

import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.nameserver.handler.JoinClusterRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.SelectAllRouteRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.SelectByTopicNameRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.UploadRouteRequestHandler;
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
    protected void registerRequestHandlers() {
        // RouteUploadRequestHandler
        registerRouteUploadRequestHandler();
        // RouteSelectAllRequestHandler
        registerRouteSelectAllRequestHandler();
        // RouteSelectRequestHandler
        registerRouteSelectRequestHandler();
        // JoinClusterRequestHandler
        registerJoinClusterRequestHandler();
    }

    /**
     * 注册上报路由请求处理器
     */
    private void registerRouteUploadRequestHandler() {
        final ExecutorService handlerExecutor = ThreadPoolUtil.getNameserverHandlerExecutorService();
        registerHandler(RequestEnum.UPLOAD_ROUTE.getCode(), UploadRouteRequestHandler.getInstance(), handlerExecutor);
    }

    /**
     * 注册查询全部路由请求处理器
     */
    private void registerRouteSelectAllRequestHandler() {
        final ExecutorService handlerExecutor = ThreadPoolUtil.getNameserverHandlerExecutorService();
        registerHandler(RequestEnum.SELECT_ALL_ROUTE.getCode(), SelectAllRouteRequestHandler.getInstance(),
            handlerExecutor);
    }

    /**
     * 注册路线查询请求处理器
     */
    private void registerRouteSelectRequestHandler() {
        final ExecutorService handlerExecutor = ThreadPoolUtil.getNameserverHandlerExecutorService();
        registerHandler(RequestEnum.SELECT_ROUTE.getCode(), SelectByTopicNameRequestHandler.getInstance(),
            handlerExecutor);
    }

    /**
     * 注册加入集群请求处理器
     */
    private void registerJoinClusterRequestHandler() {
        final ExecutorService handlerExecutor = ThreadPoolUtil.getNameserverHandlerExecutorService();
        registerHandler(RequestEnum.JOIN_CLUSTER.getCode(), JoinClusterRequestHandler.getInstance(), handlerExecutor);
    }
}
