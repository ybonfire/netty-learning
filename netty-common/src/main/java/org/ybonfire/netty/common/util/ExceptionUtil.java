package org.ybonfire.netty.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.netty.common.exception.BaseException;
import org.ybonfire.netty.common.exception.ConnectTimeoutException;
import org.ybonfire.netty.common.exception.ExceptionTypeEnum;
import org.ybonfire.netty.common.exception.RequestTimeoutException;

/**
 * 异常工具类
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:23
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionUtil {

    /**
     * @description: 构造BaseException
     * @param:
     * @return:
     * @date: 2022/05/19 10:24:23
     */
    public static BaseException exception(final ExceptionTypeEnum type) {
        switch (type) {
            case CONNECT_TIMEOUT:
                return new ConnectTimeoutException();
            case REQUEST_TIMEOUT:
                return new RequestTimeoutException();
            default:
                return new BaseException(ExceptionTypeEnum.UNKNOWN);
        }
    }

    /**
     * @description: 构造BaseException
     * @param:
     * @return:
     * @date: 2022/05/19 10:24:23
     */
    public static BaseException exception(final ExceptionTypeEnum type, final Throwable cause) {
        switch (type) {
            case CONNECT_TIMEOUT:
                return new ConnectTimeoutException(cause);
            case REQUEST_TIMEOUT:
                return new RequestTimeoutException(cause);
            default:
                return new BaseException(ExceptionTypeEnum.UNKNOWN, cause);
        }
    }
}
