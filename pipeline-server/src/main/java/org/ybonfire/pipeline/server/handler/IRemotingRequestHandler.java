package org.ybonfire.pipeline.server.handler;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * Netty请求处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:29
 */
@FunctionalInterface
public interface IRemotingRequestHandler<T extends IRemotingRequestBody> {

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022-05-18 10:29
     */
    RemotingResponse handle(final IRemotingRequest<T> request);
}
