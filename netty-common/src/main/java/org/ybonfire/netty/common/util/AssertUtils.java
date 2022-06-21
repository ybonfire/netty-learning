package org.ybonfire.netty.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 断言工具类
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 14:26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssertUtils {

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void notNull(Object object, String errorMsgTemplate, Object... params) {
        if (object == null) {
            throw new IllegalArgumentException(String.format(errorMsgTemplate, params));
        }
    }
}
