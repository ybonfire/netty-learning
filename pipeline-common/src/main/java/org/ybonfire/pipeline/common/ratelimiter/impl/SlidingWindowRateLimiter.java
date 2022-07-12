package org.ybonfire.pipeline.common.ratelimiter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.ratelimiter.IRateLimiter;
import org.ybonfire.pipeline.common.util.ExceptionUtil;

import lombok.Builder;

/**
 * 滑动窗口限流器
 *
 * @author Bo.Yuan5
 * @date 2022-07-12 14:42
 */
public final class SlidingWindowRateLimiter implements IRateLimiter {
    private static final int WINDOW_COUNT = 10;
    private final List<Window> windows = new ArrayList<>();
    private final long intervalMillisPerWindow;
    private final long acquiresPerWindow;

    public SlidingWindowRateLimiter(final long intervalMillis, final long acquires) {
        if (intervalMillis < 1000L) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }
        if (acquires < 0) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }

        this.intervalMillisPerWindow = intervalMillis / WINDOW_COUNT;
        this.acquiresPerWindow = acquires / WINDOW_COUNT;

        for (int i = 0; i < WINDOW_COUNT; ++i) {
            windows.add(Window.builder().intervalMillis(intervalMillisPerWindow).acquires(acquiresPerWindow).build());
        }
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
        return currentWindow().acquire(acquires);
    }

    /**
     * @description: 获取当前时间窗口
     * @param:
     * @return:
     * @date: 2022/07/12 17:56:05
     */
    private Window currentWindow() {
        final int index = ((int)(System.currentTimeMillis() / intervalMillisPerWindow)) % this.windows.size();
        return this.windows.get(index);
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
        public synchronized boolean acquire(final long acquires) {
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
            if (System.currentTimeMillis() - startTime > intervalMillis) {
                counter.set(0);
            }
        }
    }
}
