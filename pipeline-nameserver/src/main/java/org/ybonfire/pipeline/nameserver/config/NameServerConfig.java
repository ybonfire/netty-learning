package org.ybonfire.pipeline.nameserver.config;

import org.ybonfire.pipeline.server.config.NettyServerConfig;

import lombok.Getter;

/**
 * NameServer配置类
 *
 * @author Bo.Yuan5
 * @date 2022-07-18 17:22
 */
@Getter
public class NameServerConfig extends NettyServerConfig {
    /**
     * NameServer默认端口
     */
    private final int port = 14690;
}
