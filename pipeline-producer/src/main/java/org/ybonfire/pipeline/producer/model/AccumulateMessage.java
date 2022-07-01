package org.ybonfire.pipeline.producer.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ybonfire.pipeline.common.model.PartitionInfo;

import lombok.Getter;

/**
 * 累加消息缓存
 *
 * @author Bo.Yuan5
 * @date 2022-06-28 10:04
 */
@Getter
public class AccumulateMessage {
    private final List<MessageWrapper> batch;
    private final PartitionInfo partition;
    private volatile long lastAppendTimestamp;

    private AccumulateMessage(final MessageWrapper message, final PartitionInfo partition) {
        this.batch = new CopyOnWriteArrayList<>();
        this.batch.add(message);
        this.partition = partition;
        this.lastAppendTimestamp = System.currentTimeMillis();
    }

    /**
     * @description: 追加消息
     * @param:
     * @return:
     * @date: 2022/06/28 10:09:47
     */
    public void append(final MessageWrapper message) {
        this.batch.add(message);
        lastAppendTimestamp = System.currentTimeMillis();
    }

    /**
     * @description: 判断是否达到发送时间
     * @param:
     * @return:
     * @date: 2022/06/28 10:09:53
     */
    public boolean isTimeUpToSend(final long lingerMillis) {
        return System.currentTimeMillis() - lingerMillis > lastAppendTimestamp;
    }

    /**
     * @description: 构造AccumulateMessage
     * @param:
     * @return:
     * @date: 2022/06/28 10:11:30
     */
    public static AccumulateMessage newAccumulateMessage(final MessageWrapper message, final PartitionInfo partition) {
        return new AccumulateMessage(message, partition);
    }
}
