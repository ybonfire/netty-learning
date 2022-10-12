package org.ybonfire.pipeline.broker.constant;

import java.io.File;

import org.ybonfire.pipeline.broker.model.MessageFlushPolicyEnum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
    public static final long BROKER_REGISTER_WAITING_MILLIS = 15 * 1000L;

    /**
     * Broker注册超时响应时长
     */
    public static final long BROKER_REGISTER_TO_NAMESERVER_TIMEOUT_MILLIS = 10 * 1000L;

    /**
     * Broker存储基本路径
     */
    public static final String BROKER_STORE_BASE_PATH =
        System.getProperty("user.home") + File.separator + "pipeline" + File.separator;

    /**
     * Broker消息文件刷盘策略
     */
    public static final MessageFlushPolicyEnum MESSAGE_FLUSH_POLICY = MessageFlushPolicyEnum.SYNC;

    /**
     * 消息刷盘重试次数
     */
    public static final int MESSAGE_FLUSH_RETRY_TIMES = 3;

    /**
     * 消息刷盘超时时长
     */
    public static final long MESSAGE_FLUSH_TIMEOUT_MILLIS = 3 * 1000L;
}
