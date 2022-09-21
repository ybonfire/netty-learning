package org.ybonfire.pipeline.nameserver.client;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;

/**
 * Nameserver远程调用接口
 *
 * @author Bo.Yuan5
 * @date 2022-08-13 11:17
 */
public interface INameServerClient {

    /**
     * @description: 上报路由
     * @param:
     * @return:
     * @date: 2022/08/13 10:59:36
     */
    void uploadRoute(final String address, final IRemotingRequest request, final long timeoutMillis);
}
