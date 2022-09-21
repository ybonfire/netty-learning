package org.ybonfire.pipeline.nameserver.handler;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.nameserver.replica.publish.RouteUploadRequestPublisher;
import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

/**
 * UploadRoute请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:35
 */
public final class UploadRouteRequestHandler extends AbstractNettyRemotingRequestHandler<RouteUploadRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final RouteManageService routeManageService;
    private final RouteUploadRequestPublisher uploadRouteRequestPublisher;

    public UploadRouteRequestHandler(final RouteManageService routeManageService,
        final RouteUploadRequestPublisher uploadRouteRequestPublisher) {
        this.routeManageService = routeManageService;
        this.uploadRouteRequestPublisher = uploadRouteRequestPublisher;
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    @Override
    protected void check(final IRemotingRequest<RouteUploadRequest> request) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    @Override
    protected RemotingResponse fire(final IRemotingRequest<RouteUploadRequest> request) {
        routeManageService.uploadByBroker(request.getBody());
        uploadRouteRequestPublisher.publish(request);

        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(),
            DefaultResponse.create(ResponseEnum.SUCCESS.name()));
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    @Override
    protected void onComplete(IRemotingRequest<RouteUploadRequest> request) {

    }
}
