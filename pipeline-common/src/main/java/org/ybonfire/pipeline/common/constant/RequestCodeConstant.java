package org.ybonfire.pipeline.common.constant;

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
    /**
     * 测试请求
     */
    public static final int TEST_REQUEST_CODE = 0;
    /**
     * 消息投递请求
     */
    public static final int PRODUCER_SEND_MESSAGE_CODE = 100;
    /**
     * 上报路由请求
     */
    public static final int UPLOAD_ROUTE_CODE = 200;
    /**
     * 查询全部路由请求
     */
    public static final int SELECT_ALL_ROUTE_CODE = 201;
    /**
     * 查询指定Topic路由请求
     */
    public static final int SELECT_ROUTE_CODE = 202;
}
