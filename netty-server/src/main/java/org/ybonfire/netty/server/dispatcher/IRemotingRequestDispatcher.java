package org.ybonfire.netty.server.dispatcher;

import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.model.Pair;
import org.ybonfire.netty.common.server.handler.IRemotingRequestHandler;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * 远程调用请求分发接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 09:39
 */
public interface IRemotingRequestDispatcher<Context, Handler extends IRemotingRequestHandler<Context>> {

    /**
     * @description: 请求分发
     * @param:
     * @return:
     * @date: 2022/05/19 09:41:39
     */
    Optional<Pair<Handler, ExecutorService>> dispatch(final RemotingCommand request);

    /**
     * @description: 注册请求处理器
     * @param:
     * @return:
     * @date: 2022/05/19 09:44:49
     */
    void registerRemotingRequestHandler(final int requestCode, final Handler handler, final ExecutorService executor);
}
