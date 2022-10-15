package org.ybonfire.pipeline.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NameServerGetRoute测试
 *
 * @author Bo.Yuan5
 * @date 2022-07-18 17:26
 */
public class NameServerGetRouteTest extends NettyRemotingClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    public NameServerGetRouteTest(NettyClientConfig config) {
        super(config);
    }

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        final NettyRemotingClient client = new NameServerGetRouteTest(new NettyClientConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));

        client.start();

        final RemotingRequest request =
            RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.SELECT_ALL_ROUTE.getCode());
        client.request(request, "0:0:0:0:0:0:0:0:14690", 10 * 1000L);
        Thread.sleep(1000L);
    }

    @Override
    protected void registerResponseProcessors() {
        // RouteSelectAllRequestProcessor
        registerResponseProcessor(RequestEnum.SELECT_ALL_ROUTE.getCode(),
            response -> System.out.println(response.getBody()), EXECUTOR);
        // RouteSelectRequestProcessor
        registerResponseProcessor(RequestEnum.SELECT_ROUTE.getCode(),
            response -> System.out.println(response.getBody()), EXECUTOR);
    }
}
