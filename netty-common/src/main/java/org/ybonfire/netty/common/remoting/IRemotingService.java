package org.ybonfire.netty.common.remoting;

/**
 * 远程调用服务
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:14
 */
public interface IRemotingService {
    /**
     * @description: 启动服务
     * @param:
     * @return:
     * @date: 2022/05/18 10:15:01
     */
    void start();

    /**
     * @description: 关闭服务
     * @param:
     * @return:
     * @date: 2022/05/18 10:15:11
     */
    void shutdown();
}
