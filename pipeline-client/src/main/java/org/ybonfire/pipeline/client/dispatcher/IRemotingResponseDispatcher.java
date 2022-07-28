package org.ybonfire.pipeline.client.dispatcher;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.handler.IRemotingResponseHandler;
import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * 远程调用响应分发接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 23:59
 */
public interface IRemotingResponseDispatcher<Handler extends IRemotingResponseHandler> {

    /**
     * @description: 响应分发
     * @param:
     * @return:
     * @date: 2022/05/19 09:41:39
     */
    Optional<Pair<Handler, ExecutorService>> dispatch(final RemotingResponse response);

    /**
     * @description: 注册响应处理器
     * @param:
     * @return:
     * @date: 2022/05/19 09:44:49
     */
    void registerRemotingRequestHandler(final int responseCode, final Handler handler, final ExecutorService executor);
}
