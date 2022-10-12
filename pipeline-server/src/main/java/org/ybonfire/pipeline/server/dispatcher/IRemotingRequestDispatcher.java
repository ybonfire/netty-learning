package org.ybonfire.pipeline.server.dispatcher;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.common.model.Pair;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.server.processor.IRemotingRequestProcessor;

/**
 * 远程调用请求分发接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 09:39
 */
public interface IRemotingRequestDispatcher<Processor extends IRemotingRequestProcessor> {

    /**
     * @description: 请求分发
     * @param:
     * @return:
     * @date: 2022/05/19 09:41:39
     */
    Optional<Pair<Processor, ExecutorService>> dispatch(final IRemotingRequest request);

    /**
     * @description: 注册请求处理器
     * @param:
     * @return:
     * @date: 2022/05/19 09:44:49
     */
    void registerRemotingRequestProcessor(final int requestCode, final Processor processor,
        final ExecutorService executor);
}
