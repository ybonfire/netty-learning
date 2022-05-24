package org.ybonfire.netty.common.server;

import java.util.concurrent.ExecutorService;

import org.ybonfire.netty.common.handler.IRemotingRequestResponseHandler;
import org.ybonfire.netty.common.remoting.IRemotingService;

/**
 * 服务端接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:14
 */
public interface IRemotingServer<Context, Handler extends IRemotingRequestResponseHandler<Context>>
    extends IRemotingService {

    /**
     * @description: 注册请求处理器
     * @param:
     * @return:
     * @date: 2022/05/18 10:26:30
     */
    void registerHandler(final int requestCode, final Handler handler, final ExecutorService executor);
}
