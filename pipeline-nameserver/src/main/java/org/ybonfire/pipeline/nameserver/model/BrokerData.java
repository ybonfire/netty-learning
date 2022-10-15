package org.ybonfire.pipeline.nameserver.model;

import lombok.Builder;
import lombok.Data;

/**
 * Broker数据
 *
 * @author yuanbo
 * @date 2022-10-15 14:52
 */
@Builder
@Data
public class BrokerData {
    /**
     * BrokerId
     */
    private final String brokerId;
    /**
     * Broker角色
     */
    private final Integer role;
    /**
     * Broker地址
     */
    private final String address;
}
