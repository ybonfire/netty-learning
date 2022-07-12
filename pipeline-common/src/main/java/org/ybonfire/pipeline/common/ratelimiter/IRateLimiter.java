package org.ybonfire.pipeline.common.ratelimiter;

/**
 * 限流器接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-11 18:17
 */
public interface IRateLimiter {

    /**
     * @description: 获取通过权限
     * @param:
     * @return:
     * @date: 2022/07/12 16:34:53
     */
    boolean acquire(final int acquires);
}
