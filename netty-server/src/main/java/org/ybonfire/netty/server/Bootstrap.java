package org.ybonfire.netty.server;

import org.ybonfire.netty.server.config.NettyServerConfig;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:28
 */
public class Bootstrap {
    public static void main(String[] args) {
        final NettyRemotingServer server = new NettyRemotingServer(new NettyServerConfig());
        server.start();
    }
}
