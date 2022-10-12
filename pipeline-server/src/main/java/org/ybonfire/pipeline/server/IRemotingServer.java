package org.ybonfire.pipeline.server;

import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.remoting.IRemotingService;
import org.ybonfire.pipeline.server.processor.IRemotingRequestProcessor;

/**
 * 服务端接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:14
 */
public interface IRemotingServer<Processor extends IRemotingRequestProcessor> extends IRemotingService {

    /**
     * @description: 注册请求处理器
     * @param:
     * @return:
     * @date: 2022/05/18 10:26:30
     */
    void registerRequestProcessor(final int requestCode, final Processor processor, final ExecutorService executor);
}
