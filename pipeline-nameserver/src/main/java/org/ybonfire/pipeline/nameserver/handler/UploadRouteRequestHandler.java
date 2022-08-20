package org.ybonfire.pipeline.nameserver.handler;

import org.ybonfire.pipeline.common.constant.ResponseStatusEnum;
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
public final class UploadRouteRequestHandler
    extends AbstractNettyRemotingRequestHandler<RouteUploadRequest, DefaultResponse> {
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
    protected RemotingResponse<DefaultResponse> fire(final IRemotingRequest<RouteUploadRequest> request) {
        routeManageService.uploadByBroker(request.getBody());
        // TODO 广播信息不保证一定能发送到集群所有结点, 考虑quorum？
        uploadRouteRequestPublisher.publish(request);
        return success(request);
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    @Override
    protected RemotingResponse<DefaultResponse> onException(final IRemotingRequest<RouteUploadRequest> request,
        final Exception ex) {
        return exception(request, ex);
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

    /**
     * @description: 构造处理成功响应体
     * @param:
     * @return:
     * @date: 2022/07/29 12:30:45
     */
    private RemotingResponse<DefaultResponse> success(final IRemotingRequest<RouteUploadRequest> request) {
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseStatusEnum.SUCCESS.getCode(),
            DefaultResponse.create("success"));
    }

    /**
     * @description: 构造处理异常响应体
     * @param:
     * @return:
     * @date: 2022/07/29 12:30:45
     */
    private RemotingResponse<DefaultResponse> exception(final IRemotingRequest<RouteUploadRequest> request,
        final Exception ex) {
        // TODO 不同Exception对应不同Status
        return RemotingResponse.create(request.getId(), request.getCode(),
            ResponseStatusEnum.INTERNAL_SYSTEM_ERROR.getCode());
    }
}
