package org.ybonfire.pipeline.common.ratelimiter.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.ratelimiter.IRateLimiter;
import org.ybonfire.pipeline.common.util.ExceptionUtil;

import lombok.Builder;

/**
 * 固定窗口限流器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 18:29
 */
public final class FixedWindowRateLimiter implements IRateLimiter {
    private final Window window;

    public FixedWindowRateLimiter(final long intervalMillis, final long acquires) {
        if (intervalMillis < 1000L) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }
        if (acquires < 0) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }
        this.window = Window.builder().intervalMillis(intervalMillis).acquires(acquires).build();
    }

    /**
     * @description: 获取通过权限
     * @param:
     * @return:
     * @date: 2022/07/12 16:34:53
     */
    @Override
    public synchronized boolean acquire(final int acquires) {
        if (acquires < 0) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }
        return window.acquire(acquires);
    }

    /**
     * @description: 时间窗口
     * @author: Bo.Yuan5
     * @date: 2022/7/12
     */
    @Builder
    private static class Window {
        private final long intervalMillis;
        private final long acquires;
        private volatile long startTime;
        private final AtomicLong counter = new AtomicLong(0L);

        /**
         * @description: 获取通过权限
         * @param:
         * @return:
         * @date: 2022/07/12 16:34:53
         */
        public boolean acquire(final long acquires) {
            tryRefresh();
            return counter.getAndAdd(acquires) <= this.acquires;
        }

        /**
         * @description: 尝试刷新令牌
         * @param:
         * @return:
         * @date: 2022/07/11 18:36:20
         */
        private void tryRefresh() {
            if (System.currentTimeMillis() - startTime > intervalMillis) { // 判断当前时间是否在window时间区间之内
                counter.set(0);
            }
        }
    }
}
