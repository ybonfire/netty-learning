package org.ybonfire.pipeline.producer.sender.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.Node;
import org.ybonfire.pipeline.common.model.PartitionInfo;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.producer.client.IRemotingClient;
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
public class SenderImpl extends AbstractThreadService implements ISender {
    private static final String NAME = "Sender";
    private final BlockingQueue<MessageSendTask> taskQueue = new LinkedBlockingQueue<>();
    private IRemotingClient client;

    public SenderImpl(final IRemotingClient client) {
        super(100L);
        this.client = client;
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

        final MessageSendTask task = MessageSendTask.build(message);
        try {
            final long startTime = System.currentTimeMillis();
            this.taskQueue.put(task);
            final long timeoutMillis = message.getTimeoutMillis() - (System.currentTimeMillis() - startTime);
            if (timeoutMillis > 0L) {
                task.getLatch().await(timeoutMillis, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // ignore
        }
    }

    @Override
    protected String getName() {
        return NAME;
    }

    /**
     * @description: 消息发送
     * @param:
     * @return:
     * @date: 2022/06/28 13:53:48
     */
    @Override
    protected void execute() {
        try {
            final MessageSendTask task = this.taskQueue.take();
            doSend(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Ignore
        }
    }

    /**
     * @description: 投递消息
     * @param:
     * @return:
     * @date: 2022/06/28 16:43:45
     */
    private ProduceResult doSend(final MessageSendTask task) {
        final MessageWrapper message = task.getMessage();
        final PartitionInfo partition = message.getPartition();

        // 消息投递
        final String address = partition.tryToFindPartitionLeaderNode().map(Node::getAddress)
            .orElseThrow(() -> ExceptionUtil.exception(ExceptionTypeEnum.UNKNOWN_PARTITION_LEADER));
        final ProduceResult result = client.produce(message.getMessage(), address, message.getTimeoutMillis());
        message.setResult(result);

        // 执行回调
        message.getCallbackOptional().ifPresent(callback -> callback.onComplete(result));

        return result;
    }

    /**
     * @description: 消息发送超时流程
     * @param:
     * @return:
     * @date: 2022/07/01 13:39:37
     */
    private void onSendTimedOut() {
        throw ExceptionUtil.exception(ExceptionTypeEnum.TIMEOUT_EXCEPTION);
    }

    /**
     * @description: 消息发送任务
     * @author: Bo.Yuan5
     * @date: 2022/6/30
     */
    @Getter
    private static class MessageSendTask {
        private final MessageWrapper message;
        private final CountDownLatch latch = new CountDownLatch(1);

        private MessageSendTask(final MessageWrapper message) {
            this.message = message;
        }

        public static MessageSendTask build(final MessageWrapper message) {
            return new MessageSendTask(message);
        }
    }
}
