package org.ybonfire.pipeline.broker.client;

import org.ybonfire.pipeline.broker.model.heartbeat.HeartbeatData;

/**
 * Nameserver远程调用客户端接口
 *
 * @author yuanbo
 * @date 2022-09-23 10:51
 */
public interface INameServerClient {

    /**
     * 上报心跳
     *
     * @param heartbeatData 心跳数据
     * @param address 地址
     * @param timeoutMillis 超时,米尔斯
     */
    void heartbeat(final HeartbeatData heartbeatData, final String address, final long timeoutMillis);

}
