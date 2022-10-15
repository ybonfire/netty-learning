package org.ybonfire.pipeline.broker.heartbeat;

import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;

import java.util.List;

/**
 * Broker心跳服务接口
 *
 * @author yuanbo
 * @date 2022-09-23 14:33
 */
public interface IBrokerHeartbeatService extends ILifeCycle {

    /**
     * 向Nameserver发送心跳
     */
    void heartbeat(final List<String> nameServerAddressList);
}
