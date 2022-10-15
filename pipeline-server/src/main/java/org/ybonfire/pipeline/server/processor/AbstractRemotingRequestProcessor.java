package org.ybonfire.pipeline.server.processor;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.server.exception.ServerException;
import org.ybonfire.pipeline.server.exception.handler.ServerExceptionHandler;

/**
 * Netty远程调用请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 18:18
 */
public abstract class AbstractRemotingRequestProcessor<T extends IRemotingRequestBody>
    implements IRemotingRequestProcessor<T> {

    /**
     * @description: 处理请求
     * @param:
     * @return:
     * @date: 2022/07/01 18:19:03
     */
    @Override
    public final RemotingResponse process(final IRemotingRequest<T> request) {
        try {
            // 参数校验
            check(request);

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
    protected abstract RemotingResponse fire(final IRemotingRequest<T> request);

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    protected abstract void onComplete(final IRemotingRequest<T> request);

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:46
     */
    private RemotingResponse onException(final IRemotingRequest<T> request, final Exception ex) {
        final ServerExceptionHandler exceptionHandler = ServerExceptionHandler.getInstance();
        return ex instanceof ServerException ? exceptionHandler.handle(request, (ServerException)ex)
            : exceptionHandler.handle(request, ex);
    }
}
