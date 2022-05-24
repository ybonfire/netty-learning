package org.ybonfire.netty.common.thread.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程服务抽象类
 *
 * @author yuanbo@megvii.com
 * @date 2021-08-25 11:17
 */
@Slf4j
public abstract class AbstractThreadService implements Runnable {
    private final RefreshableCountDownLatch latch = new RefreshableCountDownLatch(1);
    protected IThreadServiceExecuteFailedCallback callback;
    protected Thread thread;
    protected volatile long intervalMillis = 5 * 1000L;
    /**
     * 标记休眠
     */
    private final AtomicBoolean paused = new AtomicBoolean(false);
    /**
     * 标记启动/关闭
     */
    private final AtomicBoolean started = new AtomicBoolean(false);
    private volatile boolean stopped = true;

    public AbstractThreadService() {}

    public AbstractThreadService(final long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    public AbstractThreadService(final IThreadServiceExecuteFailedCallback callback, final long intervalMillis) {
        this.callback = callback;
        this.intervalMillis = intervalMillis;
    }

    /**
     * @description: 启动线程
     * @param:
     * @return:
     * @date: 2021/8/25
     */
    public void start() {
        if (started.compareAndSet(false, true)) {
            this.stopped = false;
            this.thread = new Thread(this, getName());
            this.thread.setDaemon(true);

            this.thread.start();
            log.info("线程启动. name:[{}]", getName());
        }
    }

    /**
     * @description: 关闭线程
     * @param:
     * @return:
     * @date: 2021/8/26
     */
    public void stop() {
        if (started.compareAndSet(true, false)) {
            this.stopped = true;
            this.thread.interrupt();
            log.info("线程关闭. name:[{}]", getName());
        }
    }

    /**
     * @description: 主流程
     * @param:
     * @return:
     * @date: 2021/8/25
     */
    @Override
    public void run() {
        while (!stopped) {
            // 休眠
            if (isPaused()) {
                waitForRunning();
            }

            // 执行业务
            try {
                execute();
            } catch (Throwable e) {
                if (callback != null) {
                    callback.onException(this, e);
                } else {
                    log.error("ThreadService Execute failed.", e);
                }
            }

            // 暂停
            waitForRunning(this.intervalMillis);
        }
    }

    /**
     * @description: 唤醒线程
     * @param:
     * @return:
     * @date: 2021/8/25
     */
    public void wakeup() {
        if (started.get()) {
            if (paused.compareAndSet(true, false)) {
                latch.countDown();
            }
        }
    }

    /**
     * @description: 休眠线程
     * @param:
     * @return:
     * @date: 2021/8/25
     */
    public void pause() {
        if (started.get()) {
            paused.compareAndSet(false, true);
        }
    }

    /**
     * @description: 暂停线程
     * @param:
     * @return:
     * @date: 2021/8/25
     */
    private void waitForRunning(final long intervalMillis) {
        latch.reset();
        if (intervalMillis > 0) {
            try {
                latch.await(intervalMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * @description: 暂停线程
     * @param:
     * @return:
     * @date: 2021/8/25
     */
    private void waitForRunning() {
        latch.reset();

        try {
            latch.await();
        } catch (InterruptedException e) {
            // ignore
        }
    }

    protected abstract String getName();

    /**
     * @description: 具体业务流程
     * @param:
     * @return:
     * @date: 2021/8/25
     */
    protected abstract void execute();

    public boolean isPaused() {
        return paused.get();
    }

    public boolean getStarted() {
        return started.get();
    }

    /**
     * 可刷新的CountDownLatch，Copy from: Apache-RocketMQ
     * https://github.com/apache/rocketmq/blob/master/common/src/main/java/org/apache/rocketmq/common/CountDownLatch2.java
     *
     * @description: 可刷新的CountDownLatch
     * @author: Bo.Yuan5
     * @date: 2022/5/24
     */
    private static class RefreshableCountDownLatch {
        private final Sync sync;

        /**
         * Constructs a {@code CountDownLatch2} initialized with the given count.
         *
         * @param count the number of times {@link #countDown} must be invoked before threads can pass through
         *            {@link #await}
         * @throws IllegalArgumentException if {@code count} is negative
         */
        public RefreshableCountDownLatch(int count) {
            if (count < 0)
                throw new IllegalArgumentException("count < 0");
            this.sync = new Sync(count);
        }

        /**
         * Causes the current thread to wait until the latch has counted down to zero, unless the thread is
         * {@linkplain Thread#interrupt interrupted}.
         *
         * <p>
         * If the current count is zero then this method returns immediately.
         *
         * <p>
         * If the current count is greater than zero then the current thread becomes disabled for thread scheduling
         * purposes and lies dormant until one of two things happen:
         * <ul>
         * <li>The count reaches zero due to invocations of the {@link #countDown} method; or
         * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current thread.
         * </ul>
         *
         * <p>
         * If the current thread:
         * <ul>
         * <li>has its interrupted status set on entry to this method; or
         * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
         * </ul>
         * then {@link InterruptedException} is thrown and the current thread's interrupted status is cleared.
         *
         * @throws InterruptedException if the current thread is interrupted while waiting
         */
        public void await() throws InterruptedException {
            sync.acquireSharedInterruptibly(1);
        }

        /**
         * Causes the current thread to wait until the latch has counted down to zero, unless the thread is
         * {@linkplain Thread#interrupt interrupted}, or the specified waiting time elapses.
         *
         * <p>
         * If the current count is zero then this method returns immediately with the value {@code true}.
         *
         * <p>
         * If the current count is greater than zero then the current thread becomes disabled for thread scheduling
         * purposes and lies dormant until one of three things happen:
         * <ul>
         * <li>The count reaches zero due to invocations of the {@link #countDown} method; or
         * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current thread; or
         * <li>The specified waiting time elapses.
         * </ul>
         *
         * <p>
         * If the count reaches zero then the method returns with the value {@code true}.
         *
         * <p>
         * If the current thread:
         * <ul>
         * <li>has its interrupted status set on entry to this method; or
         * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
         * </ul>
         * then {@link InterruptedException} is thrown and the current thread's interrupted status is cleared.
         *
         * <p>
         * If the specified waiting time elapses then the value {@code false} is returned. If the time is less than or
         * equal to zero, the method will not wait at all.
         *
         * @param timeout the maximum time to wait
         * @param unit the time unit of the {@code timeout} argument
         * @return {@code true} if the count reached zero and {@code false} if the waiting time elapsed before the count
         *         reached zero
         * @throws InterruptedException if the current thread is interrupted while waiting
         */
        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
        }

        /**
         * Decrements the count of the latch, releasing all waiting threads if the count reaches zero.
         *
         * <p>
         * If the current count is greater than zero then it is decremented. If the new count is zero then all waiting
         * threads are re-enabled for thread scheduling purposes.
         *
         * <p>
         * If the current count equals zero then nothing happens.
         */
        public void countDown() {
            sync.releaseShared(1);
        }

        /**
         * Returns the current count.
         *
         * <p>
         * This method is typically used for debugging and testing purposes.
         *
         * @return the current count
         */
        public long getCount() {
            return sync.getCount();
        }

        public void reset() {
            sync.reset();
        }

        /**
         * Returns a string identifying this latch, as well as its state. The state, in brackets, includes the String
         * {@code "Count ="} followed by the current count.
         *
         * @return a string identifying this latch, as well as its state
         */
        public String toString() {
            return super.toString() + "[Count = " + sync.getCount() + "]";
        }

        /**
         * Synchronization control For CountDownLatch2. Uses AQS state to represent count.
         */
        private static final class Sync extends AbstractQueuedSynchronizer {
            private static final long serialVersionUID = 4982264981922014374L;

            private final int startCount;

            Sync(int count) {
                this.startCount = count;
                setState(count);
            }

            int getCount() {
                return getState();
            }

            protected int tryAcquireShared(int acquires) {
                return (getState() == 0) ? 1 : -1;
            }

            protected boolean tryReleaseShared(int releases) {
                // Decrement count; signal when transition to zero
                for (;;) {
                    int c = getState();
                    if (c == 0)
                        return false;
                    int nextc = c - 1;
                    if (compareAndSetState(c, nextc))
                        return nextc == 0;
                }
            }

            protected void reset() {
                setState(startCount);
            }
        }
    }
}
