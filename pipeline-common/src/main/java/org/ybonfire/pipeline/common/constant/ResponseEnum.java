package org.ybonfire.pipeline.common.constant;

import java.util.Optional;

import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;

/**
 * 请求码常量类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 16:49
 */
public enum ResponseEnum {
    /**
     * 成功
     */
    SUCCESS(0, IRemotingResponseBody.class),
    /**
     * 不支持的请求码
     */
    REQUEST_CODE_NOT_SUPPORTED(-1, DefaultResponse.class),
    /**
     * 系统内部异常
     */
    INTERNAL_SYSTEM_ERROR(-2, DefaultResponse.class),
    /**
     * 服务未响应
     */
    SERVER_NOT_RESPONSE(-3, DefaultResponse.class),
    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(-4, DefaultResponse.class);

    private final int code;
    private final Class<? extends IRemotingResponseBody> clazz;

    ResponseEnum(final int code) {
        this(code, null);
    }

    ResponseEnum(final int code, final Class<? extends IRemotingResponseBody> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public int getCode() {
        return code;
    }

    public Optional<Class<? extends IRemotingResponseBody>> getClazz() {
        return Optional.ofNullable(clazz);
    }

    public static ResponseEnum code(final int code) {
        for (final ResponseEnum request : ResponseEnum.values()) {
            if (request.code == code) {
                return request;
            }
        }

        return null;
    }
}
