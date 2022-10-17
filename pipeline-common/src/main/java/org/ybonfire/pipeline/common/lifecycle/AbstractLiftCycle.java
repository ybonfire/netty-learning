package org.ybonfire.pipeline.common.lifecycle;

import org.ybonfire.pipeline.common.exception.LifeCycleException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 生命周期管理基类
 *
 * @author yuanbo
 * @date 2022-10-17 17:13
 */
public abstract class AbstractLiftCycle implements ILifeCycle {
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * @description: 启动服务
     * @param:
     * @return:
     * @date: 2022/10/17 17:14:09
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            onStart();
        }
    }

    /**
     * @description: 判断服务是否已启动
     * @param:
     * @return:
     * @date: 2022/10/17 17:14:43
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭服务
     * @param:
     * @return:
     * @date: 2022/10/17 17:14:45
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            onShutdown();
        }
    }

    /**
     * @description: 确保服务已就绪
     * @param:
     * @return:
     * @date: 2022/07/14 14:37:04
     */
    protected void acquireOK() {
        if (!this.isStarted.get()) {
            throw new LifeCycleException();
        }
    }

    /**
     * @description: 服务启动流程
     * @param:
     * @return:
     * @date: 2022/10/17 17:14:38
     */
    protected abstract void onStart();

    /**
     * @description: 服务关闭流程
     * @param:
     * @return:
     * @date: 2022/10/17 17:14:40
     */
    protected abstract void onShutdown();
}
