package org.ybonfire.pipeline.common.util;

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
        if (object == null) {
            throw new IllegalArgumentException("[Assertion failed] - this argument is required; it must not be null");
        }
    }

    public static void express(boolean express, final String message) {
        if (!express) {
            throw new IllegalArgumentException("[Assertion failed] - " + message);
        }
    }

}
