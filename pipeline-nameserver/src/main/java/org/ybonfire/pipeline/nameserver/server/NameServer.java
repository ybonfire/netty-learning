package org.ybonfire.pipeline.nameserver.server;

import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;
import org.ybonfire.pipeline.nameserver.handler.SelectAllRouteRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.SelectByTopicNameRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.UploadRouteRequestHandler;
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
    private final RouteManageService routeManageService = new RouteManageService(new InMemoryRouteRepository());
    private final IRemotingRequestHandler uploadRouteRequestHandler =
        new UploadRouteRequestHandler(routeManageService);
    private final IRemotingRequestHandler selectAllRouteRequestHandler =
        new SelectAllRouteRequestHandler(routeManageService);
    private final IRemotingRequestHandler selectByTopicNameRequestHandler =
        new SelectByTopicNameRequestHandler(routeManageService);
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
        registerHandler(RequestEnum.UPLOAD_ROUTE.getCode(), uploadRouteRequestHandler, handlerExecutor);
        // RouteSelectAllRequestHandler
        registerHandler(RequestEnum.SELECT_ALL_ROUTE.getCode(), selectAllRouteRequestHandler, handlerExecutor);
        // RouteSelect
        registerHandler(RequestEnum.SELECT_ROUTE.getCode(), selectByTopicNameRequestHandler, handlerExecutor);
    }
}
