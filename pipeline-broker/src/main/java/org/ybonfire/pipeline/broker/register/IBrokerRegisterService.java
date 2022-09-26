package org.ybonfire.pipeline.broker.register;

import java.util.List;

/**
 * Broker注册服务接口
 *
 * @author yuanbo
 * @date 2022-09-23 14:33
 */
public interface IBrokerRegisterService {

    /**
     * @description: 启动Broker注册服务
     * @param:
     * @return:
     * @date: 2022/09/26 11:51:05
     */
    void start();

    /**
     * @description: 关闭Broker注册服务
     * @param:
     * @return:
     * @date: 2022/09/26 11:51:05
     */
    void shutdown();

    /**
     * 将Broker注册至NameServer
     */
    void registerToNameServer(final List<String> nameServerAddressList);
}
