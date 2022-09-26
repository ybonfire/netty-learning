package org.ybonfire.pipeline.broker.config;

import lombok.Getter;
import lombok.Setter;
import org.ybonfire.pipeline.server.config.NettyServerConfig;

/**
 * Broker配置类
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:44
 */
@Setter
@Getter
public class BrokerConfig extends NettyServerConfig {
    /**
     * Broker id. 需保证该id在NameServer集群中唯一
     */
    private final String id = "DEFAULT";
    /**
     * Broker角色配置
     */
    private final int role = 1;
}
