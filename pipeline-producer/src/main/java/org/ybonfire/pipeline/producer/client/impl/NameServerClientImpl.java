package org.ybonfire.pipeline.producer.client.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.client.handler.IRemotingResponseHandler;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectAllRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectByTopicRequest;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;
import org.ybonfire.pipeline.producer.client.INameServerClient;
import org.ybonfire.pipeline.producer.converter.TopicInfoConverter;
import org.ybonfire.pipeline.producer.handler.SelectAllRouteResponseHandler;
import org.ybonfire.pipeline.producer.handler.SelectRouteResponseHandler;

/**
 * Nameserver远程调用
 *
 * @author Bo.Yuan5
 * @date 2022-08-04 17:56
 */
public class NameServerClientImpl extends NettyRemotingClient implements INameServerClient {
    private final IRemotingResponseHandler selectAllRouteResponseHandler =
        new SelectAllRouteResponseHandler(getInflightRequestManager());
    private final IRemotingResponseHandler selectRouteSelectResponseHandler =
        new SelectRouteResponseHandler(getInflightRequestManager());

    public NameServerClientImpl() {
        this(new NettyClientConfig());
    }

    public NameServerClientImpl(final NettyClientConfig config) {
        super(config);
    }

    /**
     * @description: 注册远程调用响应处理器
     * @param:
     * @return:
     * @date: 2022/08/04 18:12:08
     */
    @Override
    protected void registerResponseHandlers() {
        // SelectAllResponseHandler
        registerHandler(RequestEnum.SELECT_ALL_ROUTE.getCode(), selectAllRouteResponseHandler, null);
        // SelectRouteResponseHandler
        registerHandler(RequestEnum.SELECT_ROUTE.getCode(), selectRouteSelectResponseHandler, null);
    }

    /**
     * @description: 发送查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:02
     */
    @Override
    public List<TopicInfo> selectAllTopicInfo(final String address) {
        final IRemotingResponse response = request(address, buildSelectAllTopicInfoRequest());
        if (response.getStatus() == ResponseEnum.SUCCESS.getCode()) {
            final RouteSelectResponse data = (RouteSelectResponse)response.getBody();
            return MapUtils.isEmpty(data.getResult()) ? Collections.emptyList() : data.getResult().values().stream()
                .map(TopicInfoConverter.getInstance()::convert).collect(Collectors.toList());
        } else { // 远程调用响应异常
            // TODO
            return Collections.emptyList();
        }
    }

    /**
     * @description: 发送查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:04
     */
    @Override
    public Optional<TopicInfo> selectTopicInfo(final String topic, final String address) {
        final IRemotingResponse response = request(address, buildSelectTopicInfoRequest(topic));
        if (response.getStatus() == ResponseEnum.SUCCESS.getCode()) {
            final RouteSelectResponse data = (RouteSelectResponse)response.getBody();
            return MapUtils.isEmpty(data.getResult()) ? Optional.empty()
                : Optional.ofNullable(data.getResult().get(topic)).map(TopicInfoConverter.getInstance()::convert);
        } else { // 远程调用响应异常
            // TODO
            return Optional.empty();
        }
    }

    /**
     * @description: 构造查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:13:21
     */
    private IRemotingRequest<RouteSelectAllRequest> buildSelectAllTopicInfoRequest() {
        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.SELECT_ALL_ROUTE.getCode());
    }

    /**
     * @description: 构造查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:13:30
     */
    private IRemotingRequest<RouteSelectByTopicRequest> buildSelectTopicInfoRequest(final String topic) {
        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.SELECT_ROUTE.getCode(),
            RouteSelectByTopicRequest.builder().topic(topic).build());
    }
}
