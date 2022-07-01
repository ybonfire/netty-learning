package org.ybonfire.pipeline.server;

import org.ybonfire.pipeline.server.config.NettyServerConfig;

/**
 * 服务端启动类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:28
 */
public class ServerStartup {
    public static void main(String[] args) {
        final NettyRemotingServer server = new NettyRemotingServer(new NettyServerConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.start();
    }
}
