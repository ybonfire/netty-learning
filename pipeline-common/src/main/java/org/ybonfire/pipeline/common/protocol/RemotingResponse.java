package org.ybonfire.pipeline.common.protocol;

import lombok.Data;

/**
 * 远程调用响应
 *
 * @author Bo.Yuan5
 * @date 2022-07-19 16:48
 */
@Data
public class RemotingResponse<T extends IRemotingResponseBody> implements IRemotingResponse<T> {
    private final String id;
    private final Integer code;
    private final Integer status;
    private final T body;

    private RemotingResponse(final String id, final Integer code, final Integer status) {
        this(id, code, status, null);
    }

    private RemotingResponse(final String id, final Integer code, final Integer status, final T body) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.body = body;
    }

    /**
     * @description: 构造远程调用响应
     * @param:
     * @return:
     * @date: 2022/07/19 16:51:43
     */
    public static RemotingResponse create(final String id, final int code, final int status) {
        return new RemotingResponse<>(id, code, status);
    }

    /**
     * @description: 构造远程调用响应
     * @param:
     * @return:
     * @date: 2022/07/19 16:51:51
     */
    public static RemotingResponse create(final String id, final int code, final int status,
        IRemotingResponseBody data) {
        return new RemotingResponse<>(id, code, status, data);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public T getBody() {
        return body;
    }
}
