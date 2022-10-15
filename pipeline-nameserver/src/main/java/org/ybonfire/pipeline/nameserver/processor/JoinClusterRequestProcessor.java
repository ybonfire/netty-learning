package org.ybonfire.pipeline.nameserver.processor;

import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.nameserver.JoinClusterRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.nameserver.model.PeerNode;
import org.ybonfire.pipeline.nameserver.replica.peer.PeerManager;
import org.ybonfire.pipeline.server.exception.RequestTypeNotSupportException;
import org.ybonfire.pipeline.server.processor.AbstractRemotingRequestProcessor;

/**
 * JoinClusterRequest请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 18:14
 */
public final class JoinClusterRequestProcessor extends AbstractRemotingRequestProcessor<JoinClusterRequest> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final JoinClusterRequestProcessor INSTANCE = new JoinClusterRequestProcessor();

    private JoinClusterRequestProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/05 18:15:35
     */
    @Override
    protected void check(final IRemotingRequest<JoinClusterRequest> request) {
        if (!isJoinClusterRequest(request)) {
            throw new RequestTypeNotSupportException();
        }
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
        PeerManager.getInstance().add(peerNode);
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

    /**
     * 判断是否为JoinClusterRequest
     *
     * @param request 请求
     * @return boolean
     */
    private boolean isJoinClusterRequest(final IRemotingRequest<JoinClusterRequest> request) {
        final Integer code = request.getCode();
        return code != null && RequestEnum.code(code) == RequestEnum.JOIN_CLUSTER;
    }

    /**
     * 获取JoinClusterRequestProcessor实例
     *
     * @return {@link JoinClusterRequestProcessor}
     */
    public static JoinClusterRequestProcessor getInstance() {
        return INSTANCE;
    }
}
