package org.ybonfire.pipeline.common.ratelimiter;

/**
 * 限流器接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 18:17
 */
public interface IRateLimiter {
    boolean acquire(final int acquires);
}
