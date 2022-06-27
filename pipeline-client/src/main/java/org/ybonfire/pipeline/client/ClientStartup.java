package org.ybonfire.pipeline.client;

import java.util.UUID;

import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.model.User;
import org.ybonfire.pipeline.common.protocol.RequestCommandCodeConstant;

/**
 * 服务端启动类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 15:23
 */
public class ClientStartup {
    public static void main(String[] args) throws InterruptedException {
        final NettyRemotingClient client = new NettyRemotingClient(new NettyClientConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));

        client.start();
        final RemotingCommand request = RemotingCommand.createRequestCommand(
            RequestCommandCodeConstant.TEST_REQUEST_CODE, UUID.randomUUID().toString(), buildUser());
        client.request("0:0:0:0:0:0:0:0:10490", request, 3 * 1000L);
        Thread.sleep(1000L);
    }

    private static User buildUser() {
        return new User("yuanb");
    }
}
