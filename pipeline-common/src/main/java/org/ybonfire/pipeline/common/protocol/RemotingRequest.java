package org.ybonfire.pipeline.common.protocol;

import lombok.Data;
import org.ybonfire.pipeline.common.util.AssertUtils;

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
     * 请求超时时长
     */
    private long timeoutMillis;
    /**
     * 请求挂起时长
     */
    private long suspendMillis;

    private RemotingRequest(final String id, final Integer code, final long timeoutMillis) {
        this(id, code, null, timeoutMillis, -1L);
    }

    private RemotingRequest(final String id, final Integer code, final T data, final long timeoutMillis) {
        this(id, code, data, timeoutMillis, -1L);
    }

    private RemotingRequest(final String id, final Integer code, final T body, final long timeoutMillis,
        final long suspendMillis) {
        AssertUtils.notNull(id);
        AssertUtils.notNull(code);
        AssertUtils.express(timeoutMillis > 0L, "Request TimeoutMillis must be greater than zero");
        this.id = id;
        this.code = code;
        this.body = body;
        this.timeoutMillis = timeoutMillis;
        this.suspendMillis = suspendMillis;
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
