package org.ybonfire.pipeline.nameserver.handler;

import org.ybonfire.pipeline.common.constant.ResponseStatusEnum;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.JoinClusterRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.nameserver.model.PeerNode;
import org.ybonfire.pipeline.nameserver.replica.peer.PeerManagerProvider;
import org.ybonfire.pipeline.server.handler.AbstractNettyRemotingRequestHandler;

/**
 * 加入集群请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 18:14
 */
public class JoinClusterRequestHandler
    extends AbstractNettyRemotingRequestHandler<JoinClusterRequest, DefaultResponse> {

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:35
     */
    @Override
    protected void check(final IRemotingRequest<JoinClusterRequest> request) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:41
     */
    @Override
    protected RemotingResponse<DefaultResponse> fire(final IRemotingRequest<JoinClusterRequest> request) {
        final PeerNode peerNode =
            PeerNode.builder().id(request.getBody().getNodeId()).address(request.getBody().getAddress()).build();
        PeerManagerProvider.getInstance().add(peerNode);
        return success(request);
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:43
     */
    @Override
    protected RemotingResponse<DefaultResponse> onException(final IRemotingRequest<JoinClusterRequest> request,
        final Exception ex) {
        return exception(request, ex);
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:49
     */
    @Override
    protected void onComplete(final IRemotingRequest<JoinClusterRequest> request) {

    }

    /**
     * @description: 构造处理成功响应体
     * @param:
     * @return:
     * @date: 2022/07/29 12:30:45
     */
    private RemotingResponse<DefaultResponse> success(final IRemotingRequest<JoinClusterRequest> request) {
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseStatusEnum.SUCCESS.getCode(),
            DefaultResponse.create("success"));
    }

    /**
     * @description: 构造处理异常响应体
     * @param:
     * @return:
     * @date: 2022/07/29 12:30:45
     */
    private RemotingResponse<DefaultResponse> exception(final IRemotingRequest<JoinClusterRequest> request,
        final Exception ex) {
        // TODO 不同Exception对应不同Status
        return RemotingResponse.create(request.getId(), request.getCode(),
            ResponseStatusEnum.INTERNAL_SYSTEM_ERROR.getCode());
    }
}
