package org.ybonfire.netty.client;

import org.ybonfire.netty.client.config.NettyClientConfig;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.protocol.RequestCodeConstant;

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
        for (int i = 0; i < 10; ++i) {
            final RemotingCommand request = RemotingCommand.createRequestCommand(RequestCodeConstant.TEST_REQUEST_CODE,
                "hello world", UUID.randomUUID().toString());
            client.request("0:0:0:0:0:0:0:0:10490", request, 30 * 1000L);
            Thread.sleep(1000L);
        }
    }
}
