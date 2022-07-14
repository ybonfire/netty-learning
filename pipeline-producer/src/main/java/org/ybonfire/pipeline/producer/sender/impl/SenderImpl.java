package org.ybonfire.pipeline.producer.sender.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.Node;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.thread.task.AbstractThreadTask;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.common.util.ThreadPoolUtil;
import org.ybonfire.pipeline.producer.client.IProduceClient;
import org.ybonfire.pipeline.producer.client.impl.ProducerClientImpl;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;
import org.ybonfire.pipeline.producer.sender.ISender;

import lombok.Getter;

/**
 * 发送器实现
 *
 * @author Bo.Yuan5
 * @date 2022-06-28 13:44
 */
public class SenderImpl implements ISender {
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final ProducerClientImpl producerClient;
    private ExecutorService produceMessageExecutor;

    public SenderImpl() {
        this.producerClient = new ProducerClientImpl((new NettyClientConfig()));
    }

    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            producerClient.start();
            produceMessageExecutor = ThreadPoolUtil.getMessageProduceExecutorService();
        }
    }

    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            if (produceMessageExecutor != null) {
                produceMessageExecutor.shutdown();
            }
        }
    }

    /**
     * @description: 发送消息，提交到任务队列
     * @param:
     * @return:
     * @date: 2022/06/28 13:47:53
     */
    @Override
    public void send(final MessageWrapper message) {
        if (message == null) {
            return;
        }

        final long startTime = System.currentTimeMillis();
        final long timeoutMillis = message.getTimeoutMillis();
        if (timeoutMillis <= 0L) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.TIMEOUT_EXCEPTION);
        }

        final MessageSendThreadTask task = buildMessageSendThreadTask(message);
        produceMessageExecutor.submit(task);
        if (timeoutMillis - (System.currentTimeMillis() - startTime) > 0L) {
            try {
                task.getLatch().await(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // ignore
            }
        }
    }

    IProduceClient getProducerClient() {
        return producerClient;
    }

    /**
     * @description: 构造MessageSendThreadTask
     * @param:
     * @return:
     * @date: 2022/07/04 10:46:15
     */
    private MessageSendThreadTask buildMessageSendThreadTask(final MessageWrapper message) {
        return new MessageSendThreadTask(message);
    }

    /**
     * @description: 消息发送任务
     * @author: Bo.Yuan5
     * @date: 2022/6/30
     */
    @Getter
    private class MessageSendThreadTask extends AbstractThreadTask {
        private final CountDownLatch latch = new CountDownLatch(1);
        private final MessageWrapper message;

        private MessageSendThreadTask(final MessageWrapper message) {
            this.message = message;
        }

        /**
         * @description: 消息投递
         * @param:
         * @return:
         * @date: 2022/07/04 10:19:29
         */
        @Override
        protected void execute() {
            final PartitionInfo partition = message.getPartition();

            try {
                // 消息投递
                final String address = partition.tryToFindPartitionLeaderNode().map(Node::getAddress)
                    .orElseThrow(() -> ExceptionUtil.exception(ExceptionTypeEnum.UNKNOWN_PARTITION_LEADER));
                final ProduceResult result =
                    SenderImpl.this.getProducerClient().produce(message, address, message.getTimeoutMillis());
                message.setResult(result);

                // 执行回调
                message.getCallbackOptional().ifPresent(callback -> callback.onComplete(result));
            } finally {
                latch.countDown();
            }
        }
    }
}
