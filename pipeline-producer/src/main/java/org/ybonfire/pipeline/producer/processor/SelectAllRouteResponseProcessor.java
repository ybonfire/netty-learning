package org.ybonfire.pipeline.producer.processor;

import org.ybonfire.pipeline.client.inflight.InflightRequestManager;
import org.ybonfire.pipeline.client.model.RemoteRequestFuture;
import org.ybonfire.pipeline.client.model.RemotingRequestFutureStateEnum;
import org.ybonfire.pipeline.client.processor.AbstractRemotingResponseProcessor;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

import java.util.Optional;

/**
 * SelectAllRoute响应处理器
 *
 * @author Bo.Yuan5
 * @date 2022-08-04 18:22
 */
public class SelectAllRouteResponseProcessor extends AbstractRemotingResponseProcessor {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final SelectAllRouteResponseProcessor INSTANCE = new SelectAllRouteResponseProcessor();
    private final InflightRequestManager inflightRequestManager = InflightRequestManager.getInstance();

    private SelectAllRouteResponseProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/04 18:28:19
     */
    @Override
    protected void check(final RemotingResponse response) {}

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/08/04 18:50:30
     */
    @Override
    protected void fire(final RemotingResponse response) {
        final String id = response.getId();
        final Optional<RemoteRequestFuture> futureOptional = inflightRequestManager.get(id);
        if (futureOptional.isPresent()) {
            final RemoteRequestFuture future = futureOptional.get();

            // 校验并修改状态
            if (future.getState() == RemotingRequestFutureStateEnum.FLIGHT) {
                future.setState(RemotingRequestFutureStateEnum.RESPOND);
            } else {
                return;
            }

            // 填充响应
            future.complete(response);
        } else {
            LOGGER.warn("未查询到对应id的在途请求");
        }
    }

    /**
     * @description: 异常处理
     * @param:
     * @return:
     * @date: 2022/08/04 18:51:02
     */
    @Override
    protected void onException(final RemotingResponse response, final Exception ex) {
        final String id = response.getId();
        final Optional<RemoteRequestFuture> futureOptional = inflightRequestManager.get(id);
        if (futureOptional.isPresent()) {
            final RemoteRequestFuture future = futureOptional.get();

            // 校验并修改状态
            if (future.getState() == RemotingRequestFutureStateEnum.FLIGHT) {
                future.setState(RemotingRequestFutureStateEnum.RESPOND);
            } else {
                return;
            }

            // 异步回调
            if (future.getCallback() != null) {
                future.getCallback().onException(ex);
            }
        } else {
            LOGGER.warn("未查询到对应id的在途请求");
        }
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/08/04 18:51:07
     */
    @Override
    protected void onComplete(final RemotingResponse response) {
        inflightRequestManager.remove(response.getId());
    }

    /**
     * 获取SelectAllRouteResponseProcessor实例
     *
     * @return {@link SelectAllRouteResponseProcessor}
     */
    public static SelectAllRouteResponseProcessor getInstance() {
        return INSTANCE;
    }
}
