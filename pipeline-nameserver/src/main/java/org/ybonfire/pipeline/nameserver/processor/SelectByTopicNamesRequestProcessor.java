package org.ybonfire.pipeline.nameserver.processor;

import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.request.nameserver.RouteSelectByTopicsRequest;
import org.ybonfire.pipeline.server.processor.AbstractRemotingRequestProcessor;

/**
 * SelectByTopicNamesRequestProcessor
 *
 * @author yuanbo
 * @date 2022-10-14 16:56
 */
public final class SelectByTopicNamesRequestProcessor
    extends AbstractRemotingRequestProcessor<RouteSelectByTopicsRequest> {
    private static final SelectByTopicNamesRequestProcessor INSTANCE = new SelectByTopicNamesRequestProcessor();

    private SelectByTopicNamesRequestProcessor() {}

    /**
     * @description: 参数校验
     * @param:
     * @return:
     * @date: 2022/07/09 15:10:48
     */
    @Override
    protected void check(IRemotingRequest<RouteSelectByTopicsRequest> request) {

    }

    /**
     * @description: 业务处理
     * @param:
     * @return:
     * @date: 2022/07/01 18:22:39
     */
    @Override
    protected RemotingResponse fire(IRemotingRequest<RouteSelectByTopicsRequest> request) {
        return null;
    }

    /**
     * @description: 处理结束流程
     * @param:
     * @return:
     * @date: 2022/07/09 15:20:21
     */
    @Override
    protected void onComplete(IRemotingRequest<RouteSelectByTopicsRequest> request) {

    }

    /**
     * 获取SelectByTopicNamesRequestProcessor实例
     *
     * @return {@link SelectByTopicNamesRequestProcessor}
     */
    public static SelectByTopicNamesRequestProcessor getInstance() {
        return INSTANCE;
    }
}
