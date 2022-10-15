package org.ybonfire.pipeline.nameserver.client;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.BrokerHeartbeatRequest;

/**
 * Nameserver远程调用接口
 *
 * @author Bo.Yuan5
 * @date 2022-08-13 11:17
 */
public interface INameServerClient {

    /**
     * @description: Broker心跳上报广播
     * @param:
     * @return:
     * @date: 2022/08/13 10:59:36
     */
    void publish(final IRemotingRequest<BrokerHeartbeatRequest> request, final String address);
}
