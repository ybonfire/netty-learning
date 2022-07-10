package org.ybonfire.pipeline.nameserver.server;

import org.ybonfire.pipeline.common.constant.RequestCodeConstant;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;
import org.ybonfire.pipeline.nameserver.handler.NameServerRequestHandler;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.nameserver.route.impl.InMemoryRouteRepository;
import org.ybonfire.pipeline.server.NettyRemotingServer;
import org.ybonfire.pipeline.server.config.NettyServerConfig;
import org.ybonfire.pipeline.server.handler.INettyRemotingRequestHandler;

import java.util.concurrent.ExecutorService;

/**
 * NameServer服务
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:13
 */
public class NameServer extends NettyRemotingServer {
    private final RouteManageService routeManageService = new RouteManageService(new InMemoryRouteRepository());
    private final INettyRemotingRequestHandler nameServerRequestHandler =
        new NameServerRequestHandler(routeManageService);
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
        // NameServerRequestHandler
        registerHandler(RequestCodeConstant.UPLOAD_ROUTE_CODE, nameServerRequestHandler, handlerExecutor);
    }
}
