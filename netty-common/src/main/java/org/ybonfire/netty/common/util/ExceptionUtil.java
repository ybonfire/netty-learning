package org.ybonfire.netty.common.util;

import org.ybonfire.netty.common.exception.BaseException;
import org.ybonfire.netty.common.exception.ExceptionTypeEnum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
        return new BaseException(type);
    }

    /**
     * @description: 构造BaseException
     * @param:
     * @return:
     * @date: 2022/05/19 10:24:23
     */
    public static BaseException exception(final ExceptionTypeEnum type, final Throwable cause) {
        return new BaseException(type, cause);
    }
}
