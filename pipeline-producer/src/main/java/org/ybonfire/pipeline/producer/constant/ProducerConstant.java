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

    /**
     * 路由更新超时时间
     */
    public static final long TIMEOUT_MILLIS_IN_UPDATE_ROUTE = 15 * 1000L;
}
