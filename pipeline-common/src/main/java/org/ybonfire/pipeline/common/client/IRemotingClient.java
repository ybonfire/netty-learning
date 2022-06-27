package org.ybonfire.pipeline.common.client;

import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.handler.IRemotingRequestResponseHandler;
import org.ybonfire.pipeline.common.remoting.IRemotingService;

import java.util.concurrent.ExecutorService;

/**
 * 客户端接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:14
 */
public interface IRemotingClient<Context, Handler extends IRemotingRequestResponseHandler<Context>>
    extends IRemotingService {

    /**
     * @description: 同步调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:20:45
     */
    RemotingCommand request(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException;

    /**
     * @description: 异步调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:20:53
     */
    void requestAsync(final String address, final RemotingCommand request, final IRequestCallback callback,
        final long timeoutMillis) throws InterruptedException;

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:21:02
     */
    void requestOneWay(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException;

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/18 10:26:30
     */
    void registerHandler(final int responseCode, final Handler handler, final ExecutorService executor);
}
