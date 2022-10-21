package org.ybonfire.pipeline.nameserver.client.impl;

import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.BrokerHeartbeatRequest;
import org.ybonfire.pipeline.nameserver.client.INameServerClient;

/**
 * Nameserver远程调用
 *
 * @author Bo.Yuan5
 * @date 2022-08-13 10:59
 */
public class NameServerClientImpl extends NettyRemotingClient implements INameServerClient {

    public NameServerClientImpl() {}

    /**
     * @description: 发送路由上报请求
     * @param:
     * @return:
     * @date: 2022/08/13 11:00:26
     */
    @Override
    public void publish(final IRemotingRequest<BrokerHeartbeatRequest> request, final String address) {
        requestOneway(request, address);
    }
}
