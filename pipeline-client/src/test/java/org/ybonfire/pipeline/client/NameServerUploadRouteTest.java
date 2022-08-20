package org.ybonfire.pipeline.client;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;

/**
 * NameServerUploadRoute测试
 *
 * @author Bo.Yuan5
 * @date 2022-07-18 17:26
 */
public class NameServerUploadRouteTest extends NettyRemotingClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ExecutorService handlerExecutor = ThreadPoolUtil.getResponseHandlerExecutorService();

    public NameServerUploadRouteTest(NettyClientConfig config) {
        super(config);
    }

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        final NettyRemotingClient client = new NameServerUploadRouteTest(new NettyClientConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));

        client.start();

        final RouteUploadRequest request = RouteUploadRequest.builder().brokerId("brokerId").address("address").role(1)
            .topics(Collections.emptyList()).dataVersion(0L).build();
        client.request("0:0:0:0:0:0:0:0:4690",
            RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.UPLOAD_ROUTE.getCode(), request, 15000L),
            3 * 1000L);
        Thread.sleep(1000L);
    }

    @Override
    protected void registerResponseHandlers() {
        // RouteUploadRequestHandler
        registerHandler(RequestEnum.UPLOAD_ROUTE.getCode(), response -> System.out.println(response.getBody()),
            handlerExecutor);
    }
}
