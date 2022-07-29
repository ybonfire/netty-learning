package org.ybonfire.pipeline.client;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-07-18 17:26
 */
public class NameServerGetRouteTester extends NettyRemotingClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ExecutorService handlerExecutor = ThreadPoolUtil.getResponseHandlerExecutorService();

    public NameServerGetRouteTester(NettyClientConfig config) {
        super(config);
    }

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        final NettyRemotingClient client = new NameServerGetRouteTester(new NettyClientConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));

        client.start();

        final RemotingRequest request =
            RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.SELECT_ALL_ROUTE.getCode());
        client.request("0:0:0:0:0:0:0:0:14690", request, 15 * 1000L);
        Thread.sleep(1000L);
    }

    @Override
    protected void registerResponseHandlers() {
        // RouteSelectAllRequestHandler
        registerHandler(RequestEnum.SELECT_ALL_ROUTE.getCode(), response -> System.out.println(response.getBody()),
            handlerExecutor);
        // RouteSelect
        registerHandler(RequestEnum.SELECT_ROUTE.getCode(), response -> System.out.println(response.getBody()),
            handlerExecutor);
    }
}
