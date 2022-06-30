package org.ybonfire.pipeline.producer;

import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.producer.config.ProducerConfig;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;
import org.ybonfire.pipeline.producer.partition.IPartitionSelector;
import org.ybonfire.pipeline.producer.sender.ISender;

/**
 * 消息生产者
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:12
 */
public class Producer {
    private final ProducerConfig config;
    private final IPartitionSelector partitionSelector;
    private final ISender sender;

    public Producer(final ProducerConfig config, final IPartitionSelector partitionSelector, final ISender sender) {
        this.config = config;
        this.partitionSelector = partitionSelector;
        this.sender = sender;
    }

    /**
     * @description:
     * @param:
     * @return:
     * @date: 2022/06/27 18:24:37
     */
    public ProduceResult produce(final Message message, final long timeoutMillis) {
        final long startTime = System.currentTimeMillis();
        // 参数合法性校验
        check(message);
        // 查询投递目的地
        final PartitionInfo partition = selectPartition(message);
        // 消息发送
        final long remainingMillis = timeoutMillis - (System.currentTimeMillis() - startTime);
        final MessageWrapper wrapper = MessageWrapper.wrap(message, partition, null, remainingMillis);
        send(wrapper);

        return wrapper.getResult();
    }

    /**
     * @description: 消息合法性校验
     * @param:
     * @return:
     * @date: 2022/06/28 09:48:42
     */
    private void check(final Message message) {}

    /**
     * @description: 选择需要投递到的Partition
     * @param:
     * @return:
     * @date: 2022/06/28 09:47:06
     */
    private PartitionInfo selectPartition(final Message message) {
        return partitionSelector.select(message)
            .orElseThrow(() -> ExceptionUtil.exception(ExceptionTypeEnum.UNKNOWN_ROUTE));
    }

    /**
     * @description: 消息缓冲累加，批量投递
     * @param:
     * @return:
     * @date: 2022/06/28 09:49:55
     */
    private void send(final MessageWrapper message) {
        sender.send(message);
    }
}
