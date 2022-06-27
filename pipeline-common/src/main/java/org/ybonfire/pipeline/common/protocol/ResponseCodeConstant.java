package org.ybonfire.pipeline.common.protocol;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 响应码常量类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 16:50
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseCodeConstant {
    /**
     * 处理成功
     */
    public static final int SUCCESS = 0;
    /**
     * 不支持的请求码
     */
    public static final int REQUEST_CODE_NOT_SUPPORTED = 1;
    /**
     * 系统内部异常
     */
    public static final int INTERNAL_SYSTEM_ERROR = 2;
    /**
     * 服务未响应异常
     */
    public static final int SERVER_NOT_RESPONSE = 3;
    /**
     * 请求超时异常
     */
    public static final int REQUEST_TIMEOUT = 4;
}
