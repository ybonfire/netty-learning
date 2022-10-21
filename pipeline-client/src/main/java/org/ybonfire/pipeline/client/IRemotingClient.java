package org.ybonfire.pipeline.client;

import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;

/**
 * 客户端接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:14
 */
public interface IRemotingClient extends ILifeCycle {

    /**
     * @description: 同步调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:20:45
     */
    IRemotingResponse request(final IRemotingRequest request, final String address, final long timeoutMillis)
        throws InterruptedException;

    /**
     * @description: 异步调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:20:53
     */
    void requestAsync(final IRemotingRequest request, final String address, final IRequestCallback callback,
        final long timeoutMillis) throws InterruptedException;

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:21:02
     */
    void requestOneway(final IRemotingRequest request, final String address) throws InterruptedException;
}
