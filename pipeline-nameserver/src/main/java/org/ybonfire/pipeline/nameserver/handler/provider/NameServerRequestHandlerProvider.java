package org.ybonfire.pipeline.nameserver.handler.provider;

import org.ybonfire.pipeline.nameserver.converter.provider.TopicInfoConverterProvider;
import org.ybonfire.pipeline.nameserver.handler.JoinClusterRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.SelectAllRouteRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.SelectByTopicNameRequestHandler;
import org.ybonfire.pipeline.nameserver.handler.UploadRouteRequestHandler;
import org.ybonfire.pipeline.nameserver.replica.publish.RouteUploadRequestPublisher;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.nameserver.route.impl.InMemoryRouteRepository;
import org.ybonfire.pipeline.server.handler.IRemotingRequestHandler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * NameServer请求处理器Provider
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 18:17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NameServerRequestHandlerProvider {
    private static final RouteManageService ROUTE_MANAGE_SERVICE =
        new RouteManageService(new InMemoryRouteRepository());
    private static final RouteUploadRequestPublisher UPLOAD_ROUTE_REQUEST_PUBLISHER = new RouteUploadRequestPublisher();
    private static final IRemotingRequestHandler UPLOAD_ROUTE_REQUEST_HANDLER =
        new UploadRouteRequestHandler(ROUTE_MANAGE_SERVICE, UPLOAD_ROUTE_REQUEST_PUBLISHER);
    private static final IRemotingRequestHandler SELECT_ALL_ROUTE_REQUEST_HANDLER =
        new SelectAllRouteRequestHandler(ROUTE_MANAGE_SERVICE, TopicInfoConverterProvider.getInstance());
    private static final IRemotingRequestHandler SELECT_BY_TOPIC_NAME_REQUEST_HANDLER =
        new SelectByTopicNameRequestHandler(ROUTE_MANAGE_SERVICE, TopicInfoConverterProvider.getInstance());
    private static final IRemotingRequestHandler JOIN_CLUSTER_REQUEST_HANDLER = new JoinClusterRequestHandler();

    /**
     * @description: 获取UploadRouteRequestHandler
     * @param:
     * @return:
     * @date: 2022/08/12 21:46:45
     */
    public static IRemotingRequestHandler getUploadRouteRequestHandler() {
        return UPLOAD_ROUTE_REQUEST_HANDLER;
    }

    /**
     * @description: 获取SelectAllRouteRequestHandler
     * @param:
     * @return:
     * @date: 2022/08/12 21:46:47
     */
    public static IRemotingRequestHandler getSelectAllRouteRequestHandler() {
        return SELECT_ALL_ROUTE_REQUEST_HANDLER;
    }

    /**
     * @description: 获取SelectByTopicNameRequestHandler
     * @param:
     * @return:
     * @date: 2022/08/12 21:46:51
     */
    public static IRemotingRequestHandler getSelectByTopicNameRequestHandler() {
        return SELECT_BY_TOPIC_NAME_REQUEST_HANDLER;
    }

    /**
     * @description: 获取JoinClusterRequestHandler
     * @param:
     * @return:
     * @date: 2022/08/12 21:46:54
     */
    public static IRemotingRequestHandler getJoinClusterRequestHandler() {
        return JOIN_CLUSTER_REQUEST_HANDLER;
    }
}
