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
public class DefaultProducerImpl implements IProducer {
    private final ProducerConfig config;
    private final IPartitionSelector partitionSelector;
    private final ISender sender;

    public DefaultProducerImpl(final ProducerConfig config, final IPartitionSelector partitionSelector,
        final ISender sender) {
        this.config = config;
        this.partitionSelector = partitionSelector;
        this.sender = sender;
    }

    /**
     * @description: 启动生产者
     * @param:
     * @return:
     * @date: 2022/07/01 13:26:59
     */
    @Override
    public void start() {
        this.sender.start();
    }

    /**
     * @description: 关闭生产者
     * @param:
     * @return:
     * @date: 2022/07/01 13:27:04
     */
    @Override
    public void shutdown() {
        this.sender.stop();
    }

    /**
     * @description: 同步消息投递
     * @param:
     * @return:
     * @date: 2022/06/27 18:24:37
     */
    @Override
    public ProduceResult produce(final Message message, final long timeoutMillis) {
        final long startTime = System.currentTimeMillis();
        // 参数合法性校验
        check(message);
        // 查询投递目的地
        final PartitionInfo partition = selectPartition(message, timeoutMillis);
        final long remainingMillis = timeoutMillis - (System.currentTimeMillis() - startTime);

        // 消息发送
        if (remainingMillis > 0L) {
            final MessageWrapper wrapper = MessageWrapper.wrap(message, partition, null, remainingMillis);
            send(wrapper); // 投递并阻塞等待投递结果
            if (wrapper.getResult() != null) { // 已获取投递结果
                return wrapper.getResult();
            }
        }

        throw ExceptionUtil.exception(ExceptionTypeEnum.TIMEOUT_EXCEPTION);
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
    private PartitionInfo selectPartition(final Message message, final long timeoutMillis) {
        return partitionSelector.select(message, timeoutMillis)
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
