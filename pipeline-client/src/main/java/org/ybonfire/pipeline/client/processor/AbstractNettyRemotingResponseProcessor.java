package org.ybonfire.pipeline.client.processor;

import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * Netty远程响应请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-09 13:57
 */
public abstract class AbstractNettyRemotingResponseProcessor implements IRemotingResponseProcessor {

    /**
     * @description: 处理响应
     * @param:
     * @return:
     * @date: 2022/07/09 13:59:34
     */
    @Override
    public final void process(final RemotingResponse response) {
        try {
            // 参数校验
            check(response);

            // 执行业务流程
            fire(response);
        } catch (final Exception ex) {
            // 执行异常处理
            onException(response, ex);
        } finally {
            onComplete(response);
        }
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    protected abstract void check(final RemotingResponse response);

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    protected abstract void fire(final RemotingResponse response);

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:46
     */
    protected abstract void onException(final RemotingResponse response, final Exception ex);

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    protected abstract void onComplete(final RemotingResponse response);
}