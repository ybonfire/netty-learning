package org.ybonfire.pipeline.common.lifecycle;

/**
 * 生命周期管理接口
 *
 * @author yuanbo
 * @date 2022-10-12 10:21
 */
public interface ILifeCycle {

    /**
     * @description: 启动服务
     * @param:
     * @return:
     * @date: 2022/10/12 10:22:20
     */
    void start();

    /**
     * @description: 判断服务是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:37
     */
    boolean isStarted();

    /**
     * @description: 关闭服务
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:33
     */
    void shutdown();
}
