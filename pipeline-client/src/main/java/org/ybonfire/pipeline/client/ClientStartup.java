package org.ybonfire.pipeline.client;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.User;
import org.ybonfire.pipeline.common.protocol.RequestCodeConstant;

/**
 * 服务端启动类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 15:23
 */
public class ClientStartup {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        final NettyRemotingClient client = new NettyRemotingClient(new NettyClientConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));

        client.start();

        final RemotingCommand request = RemotingCommand.createRequestCommand(
            RequestCodeConstant.TEST_REQUEST_CODE, UUID.randomUUID().toString(), buildUser());
        client.request("0:0:0:0:0:0:0:0:10490", request, 3 * 1000L);
        Thread.sleep(1000L);
    }

    private static Message buildUser() throws JsonProcessingException {
        final User user = new User("yuanbo");
        return Message.builder().topic("topic").key("key").payload(MAPPER.writeValueAsBytes(user)).build();
    }
}
