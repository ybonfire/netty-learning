package org.ybonfire.pipeline.common.constant;

import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.request.broker.CreateTopicRequest;
import org.ybonfire.pipeline.common.protocol.request.broker.DeleteTopicRequest;
import org.ybonfire.pipeline.common.protocol.request.broker.MessageProduceRequest;
import org.ybonfire.pipeline.common.protocol.request.broker.UpdateTopicRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.BrokerHeartbeatRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.JoinClusterRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.RouteSelectAllRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.RouteSelectByTopicRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.RouteSelectByTopicsRequest;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.common.protocol.response.nameserver.RouteSelectResponse;

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
    PRODUCE_MESSAGE(100, MessageProduceRequest.class, DefaultResponse.class),
    /**
     * 心跳注册请求
     */
    BROKER_HEARTBEAT(200, BrokerHeartbeatRequest.class, DefaultResponse.class),
    /**
     * 查询全部路由请求
     */
    SELECT_ALL_ROUTE(201, RouteSelectAllRequest.class, RouteSelectResponse.class),
    /**
     * 查询指定路由请求
     */
    SELECT_ROUTE(202, RouteSelectByTopicRequest.class, RouteSelectResponse.class),
    /**
     * 批量查询指定路由请求
     */
    SELECT_ROUTES(203, RouteSelectByTopicsRequest.class, RouteSelectResponse.class),
    /**
     * 加入集群请求
     */
    JOIN_CLUSTER(204, JoinClusterRequest.class, DefaultResponse.class),
    /**
     * 路由复制请求
     */
    ROUTE_REPLICA(205, null, DefaultResponse.class),
    /**
     * Topic创建请求
     */
    CREATE_TOPIC(300, CreateTopicRequest.class, DefaultResponse.class),
    /**
     * Topic更新请求
     */
    UPDATE_TOPIC(301, UpdateTopicRequest.class, DefaultResponse.class),
    /**
     * Topic删除请求
     */
    DELETE_TOPIC(302, DeleteTopicRequest.class, DefaultResponse.class);

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
