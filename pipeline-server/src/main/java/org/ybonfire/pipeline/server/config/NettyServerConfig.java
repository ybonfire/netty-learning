package org.ybonfire.pipeline.server.config;

import lombok.Getter;

/**
 * Netty服务端配置项
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:49
 */
@Getter
public class NettyServerConfig {
    private static final NettyServerConfig INSTANCE = new NettyServerConfig();
    /**
     * 监听端口
     */
    private final int port = 10490;
    /**
     * netty acceptor线程数
     */
    private final int acceptorThreadNums = 1;
    /**
     * netty selector线程数
     */
    private final int selectorThreadNums = 3;
    /**
     * netty worker线程数
     */
    private final int workerThreadNums = 8;
    /**
     * socket Send Buffer size;
     */
    private final int serverSocketSendBufferSize = 65535;
    /**
     * socket receive buffer size
     */
    private final int serverSocketReceiveBufferSize = 65535;

    public static NettyServerConfig getInstance() {
        return INSTANCE;
    }
}
