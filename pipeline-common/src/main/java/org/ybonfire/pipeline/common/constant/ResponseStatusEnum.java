package org.ybonfire.pipeline.common.constant;

import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;

import java.util.Optional;

/**
 * 响应状态枚举
 *
 * @author Bo.Yuan5
 * @date 2022-07-28 19:36
 */
public enum ResponseStatusEnum {
    /**
     * 成功
     */
    SUCCESS(0, null),
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

    ResponseStatusEnum(int code, Class<? extends IRemotingResponseBody> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public int getCode() {
        return code;
    }

    public Optional<Class<? extends IRemotingResponseBody>> getClazz() {
        return Optional.ofNullable(clazz);
    }

    public static ResponseStatusEnum of(final int code) {
        for (final ResponseStatusEnum status : ResponseStatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }

        return null;
    }

}
