package org.ybonfire.pipeline.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.nameserver.BrokerHeartbeatRequest;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NameServerUploadRoute测试
 *
 * @author Bo.Yuan5
 * @date 2022-07-18 17:26
 */
public class BrokerHeartbeatTest extends NettyRemotingClient {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    public BrokerHeartbeatTest() {}

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        final NettyRemotingClient client = new BrokerHeartbeatTest();

        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));

        client.start();

        final BrokerHeartbeatRequest request =
            BrokerHeartbeatRequest.builder().address("address").topicConfigs(Collections.emptyList()).build();
        client.request(
            RemotingRequest.create(UUID.randomUUID().toString(), RequestEnum.BROKER_HEARTBEAT.getCode(), request),
            "0:0:0:0:0:0:0:0:4690", 10 * 1000L);
        Thread.sleep(1000L);
    }
}
