package org.ybonfire.netty.client.config;

import lombok.Getter;

/**
 * Netty客户端配置项
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 15:33
 */
@Getter
public class NettyClientConfig {
    /**
     * netty worker线程数
     */
    private final int clientWorkerThreads = 4;
    /**
     * 连接超时阈值
     */
    private int connectTimeoutMillis = 3000;
}
