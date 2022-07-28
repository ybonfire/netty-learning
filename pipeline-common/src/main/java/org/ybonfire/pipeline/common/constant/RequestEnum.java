package org.ybonfire.pipeline.common.constant;

import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectByTopicRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;

import java.util.Optional;

/**
 * 请求码常量类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 16:49
 */
public enum RequestEnum {
    /**
     * 消息投递请求
     */
    PRODUCER_SEND_MESSAGE(100),
    /**
     * 上报路由请求
     */
    UPLOAD_ROUTE(200, RouteUploadRequest.class),
    /**
     * 查询全部路由请求
     */
    SELECT_ALL_ROUTE(201),
    /**
     * 查询指定路由请求
     */
    SELECT_ROUTE(202, RouteSelectByTopicRequest.class);

    private final int code;
    private final Class<? extends IRemotingRequestBody> clazz;

    RequestEnum(final int code) {
        this(code, null);
    }

    RequestEnum(final int code, final Class<? extends IRemotingRequestBody> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public int getCode() {
        return code;
    }

    public Optional<Class<? extends IRemotingRequestBody>> getClazz() {
        return Optional.ofNullable(clazz);
    }

    public static RequestEnum code(final int code) {
        for (final RequestEnum request : RequestEnum.values()) {
            if (request.code == code) {
                return request;
            }
        }

        return null;
    }
}
