package org.ybonfire.pipeline.nameserver.client.impl;

import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.nameserver.client.INameServerClient;

/**
 * Nameserver远程调用
 *
 * @author Bo.Yuan5
 * @date 2022-08-13 10:59
 */
public class NameServerClientImpl extends NettyRemotingClient implements INameServerClient {

    public NameServerClientImpl(final NettyClientConfig config) {
        super(config);
    }

    /**
     * @description: 发送路由上报请求
     * @param:
     * @return:
     * @date: 2022/08/13 11:00:26
     */
    @Override
    public void uploadRoute(final String address, final IRemotingRequest request, final long timeoutMillis) {
        try {
            requestOneWay(address, request, timeoutMillis);
        } catch (Exception ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.REMOTING_INVOKE_FAILED);
        }
    }

    /**
     * @description: 注册远程调响应处理器
     * @param:
     * @return:
     * @date: 2022/08/13 11:11:51
     */
    @Override
    protected void registerResponseHandlers() {

    }
}
