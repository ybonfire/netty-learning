package org.ybonfire.netty.client.dispatcher;

import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.handler.IRemotingRequestResponseHandler;
import org.ybonfire.netty.common.model.Pair;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * 远程调用响应分发接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 23:59
 */
public interface IRemotingResponseDispatcher<Context, Handler extends IRemotingRequestResponseHandler<Context>> {

    /**
     * @description: 响应分发
     * @param:
     * @return:
     * @date: 2022/05/19 09:41:39
     */
    Optional<Pair<Handler, ExecutorService>> dispatch(final RemotingCommand response);

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/19 09:44:49
     */
    void registerRemotingRequestHandler(final int responseCode, final Handler handler, final ExecutorService executor);
}
