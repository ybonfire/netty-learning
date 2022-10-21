package org.ybonfire.pipeline.client.processor.impl;

import org.ybonfire.pipeline.client.inflight.InflightRequestManager;
import org.ybonfire.pipeline.client.model.RemoteRequestFuture;
import org.ybonfire.pipeline.client.processor.IRemotingResponseProcessor;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

import java.util.Optional;

/**
 * Netty远程响应请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-07-09 13:57
 */
public class DefaultRemotingResponseProcessor implements IRemotingResponseProcessor {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final DefaultRemotingResponseProcessor INSTANCE = new DefaultRemotingResponseProcessor();

    private DefaultRemotingResponseProcessor() {}

    /**
     * @description: 处理响应
     * @param:
     * @return:
     * @date: 2022/07/09 13:59:34
     */
    @Override
    public void process(final RemotingResponse response) {
        if (response == null) {
            return;
        }

        try {
            final String id = response.getId();
            final Optional<RemoteRequestFuture> futureOptional = InflightRequestManager.getInstance().get(id);
            if (futureOptional.isPresent()) {
                final RemoteRequestFuture future = futureOptional.get();
                future.complete(response);
            } else {
                LOGGER.warn("未查询到对应id的在途请求");
            }
        } finally {
            InflightRequestManager.getInstance().remove(response.getId());
        }
    }

    /**
     * 获取DefaultRemotingResponseProcessor实例
     *
     * @return {@link DefaultRemotingResponseProcessor}
     */
    public static DefaultRemotingResponseProcessor getInstance() {
        return INSTANCE;
    }
}
