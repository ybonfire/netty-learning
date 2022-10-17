package org.ybonfire.pipeline.common.protocol;

/**
 * 远程调用响应接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-19 16:39
 */
public interface IRemotingResponse<T extends IRemotingResponseBody> extends IRemotingRequestResponse {

    /**
     * @description: 获取远程调用Id
     * @param:
     * @return:
     * @date: 2022/07/19 16:37:50
     */
    String getId();

    /**
     * @description: 获取远程调用类型码
     * @param:
     * @return:
     * @date: 2022/07/19 16:37:53
     */
    Integer getCode();

    /**
     * @description: 获取响应状态
     * @param:
     * @return:
     * @date: 2022/07/19 16:40:14
     */
    Integer getStatus();

    T getBody();
}
