package org.ybonfire.pipeline.producer.sender.impl;

import lombok.Getter;
import org.ybonfire.pipeline.client.exception.ReadTimeoutException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.thread.task.AbstractThreadTask;
import org.ybonfire.pipeline.producer.client.impl.BrokerClientImpl;
import org.ybonfire.pipeline.producer.model.MessageWrapper;
import org.ybonfire.pipeline.producer.model.ProduceResult;
import org.ybonfire.pipeline.producer.model.ProduceTypeEnum;
import org.ybonfire.pipeline.producer.sender.ISender;
import org.ybonfire.pipeline.producer.util.ThreadPoolUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 发送器实现
 *
 * @author Bo.Yuan5
 * @date 2022-06-28 13:44
 */
public class SenderImpl implements ISender {
    private static final SenderImpl INSTANCE = new SenderImpl();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final ExecutorService produceMessageExecutor = ThreadPoolUtil.getMessageProduceExecutorService();
    private final BrokerClientImpl brokerClient = new BrokerClientImpl();

    private SenderImpl() {}

    /**
     * @description: 启动发送器
     * @param:
     * @return:
     * @date: 2022/10/12 13:51:00
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            this.brokerClient.start();
        }
    }

    /**
     * @description: 判断发送器是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 13:51:08
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            this.produceMessageExecutor.shutdown();
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

        // 构造消息发送异步任务
        final MessageSendThreadTask task = buildMessageSendThreadTask(message);
        produceMessageExecutor.submit(task);

        // 阻塞等待同步投递结果
        final long timeoutMillis = message.getTimeoutMillis();
        if (isSyncMessage(message) && timeoutMillis > 0L) {
            try {
                task.getLatch().await(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // ignore
            }
        }
    }

    /**
     * @description: 判断是否是同步消息
     * @param:
     * @return:
     * @date: 2022/07/15 10:24:50
     */
    private boolean isSyncMessage(final MessageWrapper message) {
        return message.getProduceType() == null || message.getProduceType() == ProduceTypeEnum.SYNC;
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
     * 获取SenderImpl实例
     *
     * @return {@link SenderImpl}
     */
    public static SenderImpl getInstance() {
        return INSTANCE;
    }

    /**
     * @description: 消息发送任务
     * @author: Bo.Yuan5
     * @date: 2022/6/30
     */
    @Getter
    private class MessageSendThreadTask extends AbstractThreadTask {
        private final IInternalLogger LOGGER = new SimpleInternalLogger();
        private final CountDownLatch latch = new CountDownLatch(1);
        private final long startTime = System.currentTimeMillis();
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
            // 判断任务是否超时
            if (isTaskExpired()) {
                final Exception ex = new ReadTimeoutException();
                LOGGER.error("消息投递超时. message:[" + message + "]" + " exception:[" + ex + "]");
                message.getCallbackOptional().ifPresent(callback -> callback.onException(ex));
                return;
            }

            // 消息投递
            try {
                final String address = message.getPartition().getAddress();
                final long remainingMillis = System.currentTimeMillis() - startTime;
                final ProduceResult result = brokerClient.produce(message, address, remainingMillis);

                // 投递成功
                message.setResult(result);
                message.getCallbackOptional().ifPresent(callback -> callback.onComplete(result));
            } catch (final Exception ex) {
                // 投递失败
                LOGGER.error("消息投递异常. message:[" + message + "]" + " exception:[" + ex + "]");
                message.getCallbackOptional().ifPresent(callback -> callback.onException(ex));
            } finally {
                // 执行完毕，唤醒外部阻塞
                if (isSyncMessage(message)) {
                    latch.countDown();
                }
            }
        }

        /**
         * 判断任务是否过期
         *
         * @return boolean
         */
        private boolean isTaskExpired() {
            return System.currentTimeMillis() - startTime > message.getTimeoutMillis();
        }
    }
}
