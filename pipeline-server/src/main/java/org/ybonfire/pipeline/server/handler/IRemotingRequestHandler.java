package org.ybonfire.pipeline.server.handler;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * Netty请求处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:29
 */
@FunctionalInterface
public interface IRemotingRequestHandler<T extends IRemotingRequestBody, R extends IRemotingResponseBody> {
    RemotingResponse<R> handle(final IRemotingRequest<T> request);
}
