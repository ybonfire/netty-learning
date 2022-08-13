package org.ybonfire.pipeline.common.protocol;

import lombok.Data;

/**
 * 远程调用请求
 *
 * @author Bo.Yuan5
 * @date 2022-07-19 16:40
 */
@Data
public class RemotingRequest<T extends IRemotingRequestBody> implements IRemotingRequest<T> {
    private final String id;
    private final Integer code;
    private final T body;
    private long timeoutMillis = -1L;

    private RemotingRequest(final String id, final Integer code, final long timeoutMillis) {
        this(id, code, null, timeoutMillis);
    }

    private RemotingRequest(final String id, final Integer code, final T body, final long timeoutMillis) {
        this.id = id;
        this.code = code;
        this.body = body;
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * @description: 构造远程调用请求
     * @param:
     * @return:
     * @date: 2022/07/19 16:51:43
     */
    public static RemotingRequest create(final String id, final int code, final long timeoutMillis) {
        return new RemotingRequest<>(id, code, timeoutMillis);
    }

    /**
     * @description: 构造远程调用请求
     * @param:
     * @return:
     * @date: 2022/07/19 16:51:51
     */
    public static RemotingRequest create(final String id, final int code, IRemotingRequestBody data,
        final long timeoutMillis) {
        return new RemotingRequest<>(id, code, data, timeoutMillis);
    }
}
