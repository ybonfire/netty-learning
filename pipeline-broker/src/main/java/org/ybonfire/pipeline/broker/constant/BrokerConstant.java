package org.ybonfire.pipeline.broker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * Broker常量类
 *
 * @author yuanbo
 * @date 2022-09-14 15:43
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BrokerConstant {
    /**
     * TopicConfigManager锁等待时长
     */
    public static final long TOPIC_CONFIG_LOCK_WAITING_MILLIS = 3 * 1000L;

    /**
     * Broker注册响应等待时长
     */
    public static final long BROKER_REGISTER_WAITING_MILLIS = 10 * 1000L;
    /**
     * Broker存储基本路径
     */
    public static final String BROKER_STORE_BASE_PATH =
        System.getProperty("user.home") + File.separator + "pipeline" + File.separator;
}
