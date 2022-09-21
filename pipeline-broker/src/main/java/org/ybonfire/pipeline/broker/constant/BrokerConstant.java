package org.ybonfire.pipeline.broker.constant;

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
     * TopicInfo锁等待时长
     */
    public static final long TOPIC_INFO_LOCK_WAITING_MILLIS = 3000L;
}
