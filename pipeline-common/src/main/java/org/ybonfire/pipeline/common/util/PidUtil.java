package org.ybonfire.pipeline.common.util;

import java.lang.management.ManagementFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Pid工具类
 *
 * @author yuanbo
 * @date 2022-10-11 10:08
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PidUtil {
    private static final String SEPARATOR = "@";
    private static final String DEFAULT_PID = "DEFAULT_PID";

    /**
     * 获取当前程序进程id
     *
     * @return {@link String}
     */
    public static String pid() {
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName(); // format: "pid@hostname"
            return name.split(SEPARATOR)[0];
        } catch (Exception e) {
            return DEFAULT_PID;
        }
    }
}
