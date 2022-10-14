package org.ybonfire.pipeline.broker.register;

import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;

import java.util.List;

/**
 * Broker注册服务接口
 *
 * @author yuanbo
 * @date 2022-09-23 14:33
 */
public interface IBrokerRegisterService extends ILifeCycle {

    /**
     * 将Broker注册至NameServer
     */
    void registerToNameServer(final List<String> nameServerAddressList);
}
