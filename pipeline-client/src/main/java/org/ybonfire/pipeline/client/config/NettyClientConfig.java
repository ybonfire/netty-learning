package org.ybonfire.pipeline.client.config;

import lombok.Getter;

/**
 * Netty客户端配置项
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 15:33
 */
@Getter
public final class NettyClientConfig {
    /**
     * netty eventLoop线程数
     */
    private final int clientEventLoopThread = 1;
    /**
     * netty worker线程数
     */
    private final int workerThreadNums = 4;
    /**
     * 连接超时阈值
     */
    private final long connectTimeoutMillis = 3000L;
    /**
     * socket Send Buffer size;
     */
    private final int clientSocketSendBufferSize = 65535;
    /**
     * socket receive buffer size
     */
    private final int clientSocketReceiveBufferSize = 65535;
}
