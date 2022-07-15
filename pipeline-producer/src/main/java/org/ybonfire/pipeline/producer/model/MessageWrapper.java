package org.ybonfire.pipeline.producer.model;

import lombok.Getter;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.producer.callback.IMessageProduceCallback;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * Message包装类
 *
 * @author Bo.Yuan5
 * @date 2022-06-30 10:25
 */
@Getter
public class MessageWrapper {
    private final Message message;
    private final ProduceTypeEnum produceType;
    private final PartitionInfo partition;
    private final Optional<IMessageProduceCallback> callbackOptional;
    private final long timeoutMillis;
    private volatile ProduceResult result;

    private MessageWrapper(final Message message, final ProduceTypeEnum produceType, final PartitionInfo partition,
        final IMessageProduceCallback callback, final long timeoutMillis) {
        this.message = message;
        this.produceType = produceType;
        this.partition = partition;
        this.callbackOptional = Optional.ofNullable(callback);
        this.timeoutMillis = timeoutMillis;
    }

    public void setResult(ProduceResult result) {
        this.result = result;
    }

    public static MessageWrapper wrap(final Message message, final ProduceTypeEnum produceType,
        final PartitionInfo partition, final IMessageProduceCallback callback, final long timeoutMillis) {
        return new MessageWrapper(message, produceType, partition, callback, timeoutMillis);
    }
}
