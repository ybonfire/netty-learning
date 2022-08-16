package org.ybonfire.pipeline.producer.client.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.ybonfire.pipeline.client.NettyRemotingClient;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.client.handler.IRemotingResponseHandler;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectAllRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteSelectByTopicRequest;
import org.ybonfire.pipeline.common.protocol.response.RouteSelectResponse;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;
import org.ybonfire.pipeline.producer.client.INameServerClient;
import org.ybonfire.pipeline.producer.converter.TopicInfoConverter;
import org.ybonfire.pipeline.producer.converter.provider.TopicInfoConverterProvider;
import org.ybonfire.pipeline.producer.handler.SelectAllRouteResponseHandler;
import org.ybonfire.pipeline.producer.handler.SelectRouteResponseHandler;

/**
 * Nameserver远程调用
 *
 * @author Bo.Yuan5
 * @date 2022-08-04 17:56
 */
public class NameServerClientImpl extends NettyRemotingClient implements INameServerClient {
    private final TopicInfoConverter topicInfoConverter = TopicInfoConverterProvider.getInstance();
    private final IRemotingResponseHandler<RouteSelectResponse> selectAllRouteResponseHandler =
        new SelectAllRouteResponseHandler(getInflightRequestManager());
    private final IRemotingResponseHandler<RouteSelectResponse> selectRouteSelectResponseHandler =
        new SelectRouteResponseHandler(getInflightRequestManager());
    private final ExecutorService responseHandler = ThreadPoolUtil.getResponseHandlerExecutorService();

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
        registerHandler(RequestEnum.SELECT_ALL_ROUTE.getCode(), selectAllRouteResponseHandler, responseHandler);
        // SelectRouteResponseHandler
        registerHandler(RequestEnum.SELECT_ROUTE.getCode(), selectRouteSelectResponseHandler, responseHandler);
    }

    /**
     * @description: 发送查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:02
     */
    @Override
    public List<TopicInfo> selectAllTopicInfo(final String address, final long timeoutMillis) {
        try {
            final IRemotingResponse<RouteSelectResponse> response =
                request(address, buildSelectAllTopicInfoRequest(), timeoutMillis);
            if (response.getStatus() == 0) {
                final RouteSelectResponse data = response.getBody();
                return MapUtils.isEmpty(data.getResult()) ? Collections.emptyList()
                    : data.getResult().values().stream().map(topicInfoConverter::convert).collect(Collectors.toList());
            } else { // 远程调用响应异常
                // TODO
                return Collections.emptyList();
            }
        } catch (Exception ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.REMOTING_INVOKE_FAILED);
        }
    }

    /**
     * @description: 发送查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:11:04
     */
    @Override
    public Optional<TopicInfo> selectTopicInfo(final String topic, final String address, final long timeoutMillis) {
        try {
            final IRemotingResponse<RouteSelectResponse> response =
                request(address, buildSelectTopicInfoRequest(topic), timeoutMillis);
            if (response.getStatus() == 0) {
                final RouteSelectResponse data = response.getBody();

                return MapUtils.isEmpty(data.getResult()) ? Optional.empty()
                    : Optional.ofNullable(data.getResult().get(topic)).map(topicInfoConverter::convert);
            } else { // 远程调用响应异常
                // TODO
                return Optional.empty();
            }
        } catch (Exception ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.REMOTING_INVOKE_FAILED);
        }
    }

    /**
     * @description: 构造查询所有TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:13:21
     */
    private IRemotingRequest<RouteSelectAllRequest> buildSelectAllTopicInfoRequest() {
        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.SELECT_ALL_ROUTE.getCode(), -1L);
    }

    /**
     * @description: 构造查询指定TopicInfo请求
     * @param:
     * @return:
     * @date: 2022/06/29 17:13:30
     */
    private IRemotingRequest<RouteSelectByTopicRequest> buildSelectTopicInfoRequest(final String topic) {
        return RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.SELECT_ROUTE.getCode(),
            RouteSelectByTopicRequest.builder().topic(topic).build(), -1L);
    }
}