package org.ybonfire.pipeline.producer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.client.exception.ReadTimeoutException;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.producer.callback.IMessageProduceCallback;
import org.ybonfire.pipeline.producer.exception.IllegalMessageException;
import org.ybonfire.pipeline.producer.exception.ProduceTimeoutException;
import org.ybonfire.pipeline.producer.exception.RouteNotFoundException;
import org.ybonfire.pipeline.producer.metadata.NameServers;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;
import org.ybonfire.pipeline.producer.model.ProduceTypeEnum;
import org.ybonfire.pipeline.producer.partition.IPartitionSelector;
import org.ybonfire.pipeline.producer.partition.impl.RoundRobinPartitionSelector;
import org.ybonfire.pipeline.producer.route.RouteManager;
import org.ybonfire.pipeline.producer.sender.ISender;
import org.ybonfire.pipeline.producer.sender.impl.SenderImpl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息生产者
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:12
 */
public final class DefaultProducerImpl implements IProducer {
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final RouteManager routeManager;
    private final IPartitionSelector partitionSelector;
    private final ISender sender;

    public DefaultProducerImpl(final List<String> nameServerAddressList) {
        this.routeManager = new RouteManager(new NameServers(nameServerAddressList));
        this.partitionSelector = new RoundRobinPartitionSelector(this.routeManager);
        this.sender = SenderImpl.getInstance();
    }

    /**
     * @description: 启动生产者
     * @param:
     * @return:
     * @date: 2022/07/01 13:26:59
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            this.routeManager.start();
            this.sender.start();
        }
    }

    /**
     * @description: 判断生产者是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 13:54:06
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭生产者
     * @param:
     * @return:
     * @date: 2022/07/01 13:27:04
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            this.routeManager.shutdown();
            this.sender.shutdown();
        }
    }

    /**
     * @description: 同步消息投递
     * @param:
     * @return:
     * @date: 2022/06/27 18:24:37
     */
    @Override
    public ProduceResult produce(final Message message, final long timeoutMillis) {
        acquireOK();
        final ProduceResult result = doProduceMessage(message, null, timeoutMillis, ProduceTypeEnum.SYNC);
        if (result != null) {
            return result;
        }

        throw new ReadTimeoutException();
    }

    /**
     * @description: 异步消息投递
     * @param:
     * @return:
     * @date: 2022/07/15 10:07:05
     */
    @Override
    public void produce(final Message message, final IMessageProduceCallback callback, final long timeoutMillis) {
        acquireOK();
        doProduceMessage(message, callback, timeoutMillis, ProduceTypeEnum.ASYNC);
    }

    /**
     * @description: 单向消息投递
     * @param:
     * @return:
     * @date: 2022/07/15 10:07:21
     */
    @Override
    public void produce(final Message message) {
        acquireOK();
        try {
            doProduceMessage(message, null, -1L, ProduceTypeEnum.ONEWAY);
        } catch (Exception ignored) {
            // ignore
        }
    }

    /**
     * @description: 消息合法性校验
     * @param:
     * @return:
     * @date: 2022/06/28 09:48:42
     */
    private void check(final Message message) {
        if (message == null) {
            throw new IllegalMessageException("message must not be null");
        }

        if (StringUtils.isBlank(message.getTopic())) {
            throw new IllegalMessageException("topic must not blank");
        }

        if (ArrayUtils.isEmpty(message.getPayload())) {
            throw new IllegalMessageException("payload must not be empty");
        }
    }

    /**
     * @description: 执行消息投递逻辑
     * @param:
     * @return:
     * @date: 2022/07/15 10:14:55
     */
    private ProduceResult doProduceMessage(final Message message, final IMessageProduceCallback callback,
        final long timeoutMillis, final ProduceTypeEnum produceType) {
        final long startTime = System.currentTimeMillis();
        // 参数合法性校验
        check(message);

        // 查询投递目的地
        final PartitionInfo partition = selectPartition(message, timeoutMillis);

        // 消息发送
        final long remainingMillis = timeoutMillis - (System.currentTimeMillis() - startTime);
        if (produceType == ProduceTypeEnum.ONEWAY || remainingMillis > 0L) {
            final MessageWrapper wrapper =
                MessageWrapper.wrap(message, produceType, partition, callback, remainingMillis);
            send(wrapper); // 投递消息，如果投递类型为同步，sender会在内部进行阻塞等待响应
            return wrapper.getResult();
        }

        throw new ProduceTimeoutException();
    }

    /**
     * @description: 选择需要投递到的Partition
     * @param:
     * @return:
     * @date: 2022/06/28 09:47:06
     */
    private PartitionInfo selectPartition(final Message message, final long timeoutMillis) {
        // TODO 未查找到对应路由时的处理
        return partitionSelector.select(message, timeoutMillis).orElseThrow(RouteNotFoundException::new);
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

    /**
     * @description: 确保服务已就绪
     * @param:
     * @return:
     * @date: 2022/07/14 14:37:04
     */
    private void acquireOK() {
        if (!this.isStarted.get()) {
            throw new LifeCycleException();
        }
    }
}
