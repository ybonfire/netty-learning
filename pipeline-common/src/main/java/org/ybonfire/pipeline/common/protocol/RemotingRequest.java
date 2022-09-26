package org.ybonfire.pipeline.common.protocol;

import org.ybonfire.pipeline.common.util.AssertUtils;

import lombok.Data;

/**
 * 远程调用请求
 *
 * @author Bo.Yuan5
 * @date 2022-07-19 16:40
 */
@Data
public class RemotingRequest<T extends IRemotingRequestBody> implements IRemotingRequest<T> {
    /**
     * 请求唯一Id
     */
    private final String id;
    /**
     * 请求类型码
     */
    private final Integer code;
    /**
     * 请求体
     */
    private final T body;
    /**
     * 请求挂起时长
     */
    private long suspendMillis;

    private RemotingRequest(final String id, final Integer code) {
        this(id, code, null, -1L);
    }

    private RemotingRequest(final String id, final Integer code, final T data) {
        this(id, code, data, -1L);
    }

    private RemotingRequest(final String id, final Integer code, final T body, final long suspendMillis) {
        AssertUtils.notNull(id);
        AssertUtils.notNull(code);
        this.id = id;
        this.code = code;
        this.body = body;
        this.suspendMillis = suspendMillis;
    }

    /**
     * @description: 构造远程调用请求
     * @param:
     * @return:
     * @date: 2022/07/19 16:51:43
     */
    public static RemotingRequest create(final String id, final int code) {
        return new RemotingRequest<>(id, code);
    }

    /**
     * @description: 构造远程调用请求
     * @param:
     * @return:
     * @date: 2022/07/19 16:51:51
     */
    public static RemotingRequest create(final String id, final int code, IRemotingRequestBody data) {
        return new RemotingRequest<>(id, code, data);
    }
}
