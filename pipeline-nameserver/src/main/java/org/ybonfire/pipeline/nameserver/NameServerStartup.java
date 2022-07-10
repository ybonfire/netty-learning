package org.ybonfire.pipeline.nameserver;

import org.ybonfire.pipeline.nameserver.server.NameServer;
import org.ybonfire.pipeline.server.config.NettyServerConfig;

/**
 * NameServer启动器
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 17:12
 */
public class NameServerStartup {

    /**
     * @description: 启动NameServer
     * @param:
     * @return:
     * @date: 2022/07/01 17:22:30
     */
    public static void main(String[] args) {
        final NameServer server = new NameServer(new NettyServerConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.start();
    }
}
