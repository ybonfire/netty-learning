package org.ybonfire.pipeline.common.ratelimiter.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.ybonfire.pipeline.common.ratelimiter.IRateLimiter;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;

/**
 * 令牌桶限流器
 *
 * @author Bo.Yuan5
 * @date 2022-07-12 17:58
 */
public final class TokenBucketRateLimiter implements IRateLimiter {
    private final long tickMillis;
    private final long tokenPerTicket;
    private final long capacity;
    private final AtomicLong tokenBucket;
    private final AbstractThreadService tokenGeneratorTask = new TokenGeneratorTask();

    public TokenBucketRateLimiter(final long tickMillis, final long tokenPerTicket, final long capacity) {
        if (tickMillis < 1000L) {
            throw new IllegalArgumentException("tickMillis must be more than 1000");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be more than 0");
        }

        this.tickMillis = tickMillis;
        this.tokenPerTicket = tokenPerTicket;
        this.capacity = capacity;
        this.tokenBucket = new AtomicLong(capacity);
    }

    /**
     * @description: 获取通过权限
     * @param:
     * @return:
     * @date: 2022/07/12 18:01:00
     */
    @Override
    public synchronized boolean acquire(final int acquires) {
        return tokenBucket.getAndAccumulate(tokenPerTicket, (x, y) -> Math.max(x - y, 0L)) > 0L;
    }

    /**
     * @description: 令牌生成线程任务
     * @param:
     * @return:
     * @date: 2022/07/12 18:37:04
     */
    private class TokenGeneratorTask extends AbstractThreadService {
        private static final String NAME = "TokenGeneratorTask";
        private long lastTickTimestamp = -1L;

        TokenGeneratorTask() {
            super(200L);
        }

        @Override
        protected String getName() {
            return NAME;
        }

        /**
         * @description: 令牌生成
         * @param:
         * @return:
         * @date: 2022/07/12 18:36:36
         */
        @Override
        protected void execute() {
            if (isTimeUp()) {
                tokenBucket.accumulateAndGet(tokenPerTicket, (x, y) -> Math.min(x + y, capacity));
                this.lastTickTimestamp = System.currentTimeMillis();
            }
        }

        /**
         * @description: 判断是否达到令牌生成时间
         * @param:
         * @return:
         * @date: 2022/07/12 18:18:31
         */
        private boolean isTimeUp() {
            return System.currentTimeMillis() - lastTickTimestamp > tickMillis;
        }
    }
}
