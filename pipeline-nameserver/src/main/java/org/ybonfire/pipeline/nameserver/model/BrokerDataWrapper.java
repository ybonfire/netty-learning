package org.ybonfire.pipeline.nameserver.model;

import lombok.Getter;

/**
 * BrokerData包装类
 *
 * @author yuanbo
 * @date 2022-10-15 15:41
 */
@Getter
public class BrokerDataWrapper {
    private final BrokerData brokerData;
    private final long lastUploadTimestamp;

    private BrokerDataWrapper(final BrokerData brokerData) {
        this.brokerData = brokerData;
        this.lastUploadTimestamp = System.currentTimeMillis();
    }

    public static BrokerDataWrapper wrap(final BrokerData brokerData) {
        return new BrokerDataWrapper(brokerData);
    }
}
