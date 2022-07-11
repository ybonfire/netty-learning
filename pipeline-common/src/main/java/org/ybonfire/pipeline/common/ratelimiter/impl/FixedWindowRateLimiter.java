package org.ybonfire.pipeline.common.ratelimiter.impl;

import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.ratelimiter.IRateLimiter;
import org.ybonfire.pipeline.common.util.ExceptionUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 固定窗口限流器
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 18:29
 */
public class FixedWindowRateLimiter implements IRateLimiter {
    private final int intervalMillisPerWindow;
    private final int acquiresPerWindow;
    private final AtomicInteger counter = new AtomicInteger();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public FixedWindowRateLimiter(final int intervalMillisPerWindow, final int acquiresPerWindow) {
        if (intervalMillisPerWindow < 1000L) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }
        if (acquiresPerWindow < 0) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }
        this.intervalMillisPerWindow = intervalMillisPerWindow;
        this.acquiresPerWindow = acquiresPerWindow;
        this.scheduledExecutorService.scheduleAtFixedRate(this::refresh, 0L, 200L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean acquire(final int acquires) {
        return counter.getAndAdd(acquires) <= acquiresPerWindow;
    }

    /**
     * @description: 刷新令牌
     * @param:
     * @return:
     * @date: 2022/07/11 18:36:20
     */
    private void refresh() {
        counter.set(0);
    }
}
