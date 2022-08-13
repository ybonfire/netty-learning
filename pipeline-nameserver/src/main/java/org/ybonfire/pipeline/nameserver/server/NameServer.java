package org.ybonfire.pipeline.nameserver.server;

import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;
import org.ybonfire.pipeline.nameserver.converter.provider.TopicInfoConverterProvider;
import org.ybonfire.pipeline.nameserver.handler.JoinClusterRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.SelectAllRouteRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.SelectByTopicNameRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.UploadRouteRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.provider.NameServerRequestHandlerProvider;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.nameserver.route.impl.InMemoryRouteRepository;
import org.ybonfire.pipeline.server.NettyRemotingServer;
import org.ybonfire.pipeline.server.config.NettyServerConfig;
import org.ybonfire.pipeline.server.handler.IRemotingRequestHandler;

/**
 * NameServer服务
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:13
 */
public final class NameServer extends NettyRemotingServer {
    private final ExecutorService handlerExecutor = ThreadPoolUtil.getNameserverHandlerExecutorService();

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
        registerHandler(RequestEnum.UPLOAD_ROUTE.getCode(),
            NameServerRequestHandlerProvider.getUploadRouteRequestHandler(), handlerExecutor);
        // RouteSelectAllRequestHandler
        registerHandler(RequestEnum.SELECT_ALL_ROUTE.getCode(),
            NameServerRequestHandlerProvider.getSelectAllRouteRequestHandler(), handlerExecutor);
        // RouteSelectRequestHandler
        registerHandler(RequestEnum.SELECT_ROUTE.getCode(),
            NameServerRequestHandlerProvider.getSelectByTopicNameRequestHandler(), handlerExecutor);
        // JoinClusterRequestHandler
        registerHandler(RequestEnum.JOIN_CLUSTER.getCode(),
            NameServerRequestHandlerProvider.getJoinClusterRequestHandler(), handlerExecutor);
    }
}
