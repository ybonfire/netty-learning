package org.ybonfire.pipeline.producer.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Producer常量类
 *
 * @author Bo.Yuan5
 * @date 2022-07-15 10:41
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProducerConstant {
    public static final long DEFAULT_TIMEOUT_MILLIS_IN_PRODUCE_ONEWAY = 15 * 1000L;
}
