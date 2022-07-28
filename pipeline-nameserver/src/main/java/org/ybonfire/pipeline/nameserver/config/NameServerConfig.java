package org.ybonfire.pipeline.nameserver.config;

import lombok.Getter;
import org.ybonfire.pipeline.server.config.NettyServerConfig;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-07-18 17:22
 */
@Getter
public class NameServerConfig extends NettyServerConfig {
    private int port = 4690;
}
