package org.ybonfire.pipeline.common.constant;

import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;

/**
 * 请求码常量类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 16:49
 */
public enum ResponseEnum {
    // #################### Common #################### //

    /**
     * 成功
     */
    SUCCESS(0, IRemotingResponseBody.class),
    /**
     * 不支持的请求类型
     */
    REQUEST_TYPE_NOT_SUPPORTED(-1, DefaultResponse.class),
    /**
     * 异常参数请求
     */
    BAD_REQUEST(-2, DefaultResponse.class),
    /**
     * 内部服务异常
     */
    INTERNAL_SERVER_ERROR(-3, DefaultResponse.class),
    /**
     * 未知异常
     */
    UNKNOWN_ERROR(-100, DefaultResponse.class),

    // #################### Broker #################### //

    /**
     * 服务器角色不支持当前请求
     */
    SERVER_ROLE_NOT_SUPPORTED(-200, DefaultResponse.class),
    /**
     * 消息文件创建失败
     */
    MESSAGE_FILE_CREATE_FAILED(-201, DefaultResponse.class),
    /**
     * 消息文件没有足够空间
     */
    MESSAGE_FILE_NOT_ENOUGH_SPACE(-202, DefaultResponse.class),
    /**
     * 消息写入失败
     */
    MESSAGE_WRITE_FAILED(-203, DefaultResponse.class),
    /**
     * 消息刷盘超时
     */
    MESSAGE_FLUSH_DISK_TIMEOUT(-204, DefaultResponse.class),
    /**
     * Topic重复创建
     */
    TOPIC_ALREADY_CREATED(-210, DefaultResponse.class),
    /**
     * 未知Topic
     */
    TOPIC_NOT_FOUND(-211, DefaultResponse.class),;

    private final int code;
    private final Class<? extends IRemotingResponseBody> clazz;

    ResponseEnum(final int code, final Class<? extends IRemotingResponseBody> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public int getCode() {
        return code;
    }

    public Class<? extends IRemotingResponseBody> getClazz() {
        return clazz;
    }

    public static ResponseEnum of(final int code) {
        for (final ResponseEnum request : ResponseEnum.values()) {
            if (request.code == code) {
                return request;
            }
        }

        return null;
    }
}
