package org.ybonfire.pipeline.common.constant;

import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.request.MessageProduceRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectAllRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectByTopicRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;

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
    PRODUCER_SEND_MESSAGE(100, MessageProduceRequest.class, DefaultResponse.class),
    /**
     * 上报路由请求
     */
    UPLOAD_ROUTE(200, RouteUploadRequest.class, DefaultResponse.class),
    /**
     * 查询全部路由请求
     */
    SELECT_ALL_ROUTE(201, RouteSelectAllRequest.class, RouteSelectResponse.class),
    /**
     * 查询指定路由请求
     */
    SELECT_ROUTE(202, RouteSelectByTopicRequest.class, RouteSelectResponse.class);

    private final int code;
    private final Class<? extends IRemotingRequestBody> requestClazz;
    private final Class<? extends IRemotingResponseBody> responseClazz;

    RequestEnum(final int code, final Class<? extends IRemotingRequestBody> requestClazz,
        final Class<? extends IRemotingResponseBody> responseClazz) {
        this.code = code;
        this.requestClazz = requestClazz;
        this.responseClazz = responseClazz;
    }

    public int getCode() {
        return code;
    }

    public Optional<Class<? extends IRemotingRequestBody>> getRequestClazz() {
        return Optional.ofNullable(requestClazz);
    }

    public Optional<Class<? extends IRemotingResponseBody>> getResponseClazz() {
        return Optional.ofNullable(responseClazz);
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
