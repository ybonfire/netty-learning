package org.ybonfire.pipeline.common.protocol;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 请求码常量类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 16:49
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestCodeConstant {
    public static final int TEST_REQUEST_CODE = 0;
    public static final int SELECT_ALL_ROUTE_CODE = 1;
    public static final int SELECT_ROUTE_CODE = 2;
    public static final int PRODUCE_MESSAGE_CODE = 3;
}
