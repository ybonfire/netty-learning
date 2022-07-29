package org.ybonfire.pipeline.server.handler;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * Netty远程调用请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 18:18
 */
public abstract class AbstractNettyRemotingRequestHandler<T extends IRemotingRequestBody,
    R extends IRemotingResponseBody> implements IRemotingRequestHandler<T, R> {

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/07/01 18:19:03
     */
    @Override
    public final RemotingResponse<R> handle(final IRemotingRequest<T> request) {
        // 参数校验
        check(request);

        try {
            // 执行业务流程
            return fire(request);
        } catch (final Exception ex) {
            // 执行异常处理
            return onException(request, ex);
        } finally {
            onComplete(request);
        }
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    protected abstract void check(final IRemotingRequest<T> request);

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    protected abstract RemotingResponse<R> fire(final IRemotingRequest<T> request);

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:46
     */
    protected abstract RemotingResponse<R> onException(final IRemotingRequest<T> request, final Exception ex);

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    protected abstract void onComplete(final IRemotingRequest<T> request);
}
