package org.ybonfire.pipeline.nameserver.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * NameServer常量类
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 15:08
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NameServerConstant {
    public static final long ROUTE_INFO_TTL_MILLIS = 15 * 1000L;
}
