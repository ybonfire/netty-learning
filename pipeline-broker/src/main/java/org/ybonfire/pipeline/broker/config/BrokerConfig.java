package org.ybonfire.pipeline.broker.config;

import lombok.Data;
import org.ybonfire.pipeline.server.config.NettyServerConfig;

/**
 * Broker配置类
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:44
 */
@Data
public class BrokerConfig extends NettyServerConfig {
    private static final BrokerConfig INSTANCE = new BrokerConfig();

    private BrokerConfig() {}

    /**
     * Broker id. 需保证该id在NameServer集群中唯一
     */
    private final String id = "DEFAULT";
    /**
     * Broker角色配置
     */
    private final int role = 1;

    public static BrokerConfig getInstance() {
        return INSTANCE;
    }
}
