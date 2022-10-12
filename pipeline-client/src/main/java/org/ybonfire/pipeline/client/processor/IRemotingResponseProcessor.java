package org.ybonfire.pipeline.client.processor;

import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * Netty响应处理器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:29
 */
@FunctionalInterface
public interface IRemotingResponseProcessor {

    /**
     * @description: 处理远程调用响应
     * @param:
     * @return:
     * @date: 2022/07/19 19:08:47
     */
    void process(final RemotingResponse response);
}
