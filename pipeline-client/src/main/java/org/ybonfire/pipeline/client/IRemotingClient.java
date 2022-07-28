package org.ybonfire.pipeline.client;

import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.handler.IRemotingResponseHandler;
import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.remoting.IRemotingService;

/**
 * 客户端接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:14
 */
public interface IRemotingClient<Handler extends IRemotingResponseHandler> extends IRemotingService {

    /**
     * @description: 同步调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:20:45
     */
    IRemotingResponse request(final String address, final IRemotingRequest request, final long timeoutMillis)
        throws InterruptedException;

    /**
     * @description: 异步调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:20:53
     */
    void requestAsync(final String address, final IRemotingRequest request, final IRequestCallback callback,
        final long timeoutMillis) throws InterruptedException;

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:21:02
     */
    void requestOneWay(final String address, final IRemotingRequest request, final long timeoutMillis)
        throws InterruptedException;

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/18 10:26:30
     */
    void registerHandler(final int responseCode, final Handler handler, final ExecutorService executor);
}
