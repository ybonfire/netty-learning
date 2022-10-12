package org.ybonfire.pipeline.client;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * NameServerUploadRoute测试
 *
 * @author Bo.Yuan5
 * @date 2022-07-18 17:26
 */
public class NameServerUploadRouteTest extends NettyRemotingClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    public NameServerUploadRouteTest(NettyClientConfig config) {
        super(config);
    }

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        final NettyRemotingClient client = new NameServerUploadRouteTest(new NettyClientConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));

        client.start();

        final RouteUploadRequest request =
            RouteUploadRequest.builder().address("address").topics(Collections.emptyList()).build();
        client.request("0:0:0:0:0:0:0:0:4690",
            RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.UPLOAD_ROUTE.getCode(), request),
            10 * 1000L);
        Thread.sleep(1000L);
    }

    @Override
    protected void registerResponseProcessors() {
        // RouteUploadRequestProcessor
        registerResponseProcessor(RequestEnum.UPLOAD_ROUTE.getCode(), response -> System.out.println(response.getBody()),
            EXECUTOR);
    }
}
