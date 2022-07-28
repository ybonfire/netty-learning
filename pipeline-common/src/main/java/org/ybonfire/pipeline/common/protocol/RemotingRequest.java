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

    private RemotingRequest(final String id, final Integer code) {
        this(id, code, null);
    }

    private RemotingRequest(final String id, final Integer code, final T body) {
        this.id = id;
        this.code = code;
        this.body = body;
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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public T getBody() {
        return body;
    }
}
