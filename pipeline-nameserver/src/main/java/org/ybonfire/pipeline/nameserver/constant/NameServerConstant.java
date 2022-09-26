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
    /**
     * 路由信息有效时长
     */
    public static final long ROUTE_INFO_TTL_MILLIS = 15 * 1000L;
    /**
     * 路由上报广播超时尺长
     */
    public static final long ROUTE_UPLOAD_PUBLISH_TIME_OUT_MILLIS = 10 * 1000L;
}
