package org.ybonfire.pipeline.nameserver.handler;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
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
public class JoinClusterRequestHandler extends AbstractNettyRemotingRequestHandler<JoinClusterRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();

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
    protected RemotingResponse fire(final IRemotingRequest<JoinClusterRequest> request) {
        final PeerNode peerNode =
            PeerNode.builder().id(request.getBody().getNodeId()).address(request.getBody().getAddress()).build();
        PeerManagerProvider.getInstance().add(peerNode);
        return RemotingResponse.create(request.getId(), request.getCode(), ResponseEnum.SUCCESS.getCode(),
            DefaultResponse.create(ResponseEnum.SUCCESS.name()));
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
}
