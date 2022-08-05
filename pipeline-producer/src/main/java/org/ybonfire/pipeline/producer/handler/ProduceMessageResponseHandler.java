package org.ybonfire.pipeline.producer.handler;

import org.ybonfire.pipeline.client.handler.AbstractNettyRemotingResponseHandler;
import org.ybonfire.pipeline.client.manager.InflightRequestManager;
import org.ybonfire.pipeline.client.model.RemoteRequestFuture;
import org.ybonfire.pipeline.client.model.RemotingRequestFutureStateEnum;
import org.ybonfire.pipeline.common.constant.ResponseStatusEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;

import java.util.Optional;

/**
 * ProduceMessage响应处理器
 *
 * @author Bo.Yuan5
 * @date 2022-08-04 18:24
 */
public class ProduceMessageResponseHandler extends AbstractNettyRemotingResponseHandler<DefaultResponse> {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final InflightRequestManager inflightRequestManager;

    public ProduceMessageResponseHandler(final InflightRequestManager inflightRequestManager) {
        this.inflightRequestManager = inflightRequestManager;
    }

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/08/04 18:28:19
     */
    @Override
    protected void check(final RemotingResponse<DefaultResponse> response) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/08/04 18:50:30
     */
    @Override
    protected void fire(final RemotingResponse<DefaultResponse> response) {
        final String id = response.getId();
        final Optional<RemoteRequestFuture> futureOptional = inflightRequestManager.get(id);
        if (futureOptional.isPresent()) {
            final RemoteRequestFuture future = futureOptional.get();

            // 修改状态
            if (future.getState() != RemotingRequestFutureStateEnum.FLIGHT) {
                future.setState(RemotingRequestFutureStateEnum.RESPOND);
            }

            // 填充响应
            future.complete(response);

            // 异步回调
            if (future.getCallback() != null) {
                final ResponseStatusEnum status = ResponseStatusEnum.of(response.getStatus());
                if (status != null) {
                    if (status == ResponseStatusEnum.SUCCESS) {
                        future.getCallback().onSuccess(response);
                    } else {
                        future.getCallback().onException(response);
                    }
                }
            }
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
    protected void onException(final RemotingResponse<DefaultResponse> response, final Exception ex) {
        LOGGER.error("响应处理异常", ex);
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/08/04 18:51:07
     */
    @Override
    protected void onComplete(final RemotingResponse<DefaultResponse> response) {
        inflightRequestManager.remove(response.getId());
    }
}
