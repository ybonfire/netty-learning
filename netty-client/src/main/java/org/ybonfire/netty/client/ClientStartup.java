package org.ybonfire.netty.client;

import org.ybonfire.netty.client.config.NettyClientConfig;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.model.User;
import org.ybonfire.netty.common.protocol.RequestCommandCodeConstant;

import java.util.UUID;

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
        return new User("ybonfire");
    }
}
