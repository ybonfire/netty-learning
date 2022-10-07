package org.ybonfire.pipeline.broker.store.message.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.exception.MessageFileCreateException;
import org.ybonfire.pipeline.broker.model.MessageFlushJob;
import org.ybonfire.pipeline.broker.model.MessageFlushPolicyEnum;
import org.ybonfire.pipeline.broker.model.MessageFlushResultEnum;
import org.ybonfire.pipeline.broker.store.file.MappedFile;
import org.ybonfire.pipeline.broker.store.index.IMessageIndexConstructService;
import org.ybonfire.pipeline.broker.store.index.impl.MessageIndexConstructServiceImpl;
import org.ybonfire.pipeline.broker.store.message.IMessageStoreService;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;
import org.ybonfire.pipeline.server.exception.MessageFlushTimeoutException;
import org.ybonfire.pipeline.server.exception.MessageWriteFailedException;

/**
 * 消息存储服务
 *
 * @author yuanbo
 * @date 2022-09-14 18:30
 */
public class DefaultMessageStoreServiceImpl implements IMessageStoreService {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final String MESSAGE_STORE_BASE_PATH = BrokerConstant.BROKER_STORE_BASE_PATH + "message";
    private final Map<String/*topic*/, Map<Integer/*partitionId*/, MappedFile>> mappedFileTable = new HashMap<>();
    private final IMessageIndexConstructService messageIndexConstructService = new MessageIndexConstructServiceImpl();
    private final MessageFileFlushThreadService messageFileFlushThreadService = new MessageFileFlushThreadService();
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * @description: 启动消息存储服务
     * @param:
     * @return:
     * @date: 2022/09/21 14:47:02
     */
    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            messageFileFlushThreadService.start();
        }
    }

    /**
     * @description: 存储消息
     * @param:
     * @return:
     * @date: 2022/09/14 18:30:19
     */
    @Override
    public void store(final String topic, final int partitionId, final byte[] data) {
        // 确保文件已创建完毕
        ensureMappedFileTableOK(topic, partitionId);

        // 查询指定TopicPartition的消息文件
        final Optional<MappedFile> mappedFileOptional = tryToFindMappedFileByTopicPartition(topic, partitionId);
        if (mappedFileOptional.isPresent()) {
            final MappedFile mappedFile = mappedFileOptional.get();

            // 写入数据
            write(mappedFile, data);

            // 提交刷盘任务
            submitMessageFlushJob(mappedFile);
        }
    }

    /**
     * @description: 关闭消息存储服务
     * @param:
     * @return:
     * @date: 2022/09/21 14:47:17
     */
    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            messageFileFlushThreadService.stop();
        }
    }

    /**
     * 确保MappedFile已创建好文件
     *
     * @param topic 主题
     * @param partitionId 分区id
     */
    private synchronized void ensureMappedFileTableOK(final String topic, final int partitionId) {
        final Map<Integer, MappedFile> mappedFileGroupByTopic =
            mappedFileTable.computeIfAbsent(topic, k -> new HashMap<>());
        if (mappedFileGroupByTopic.get(partitionId) == null) {
            try {
                final String filename = buildMessageFilePath(topic, partitionId);
                final MappedFile mappedFile = MappedFile.create(topic, partitionId, filename);
                mappedFileGroupByTopic.put(partitionId, mappedFile);
                messageIndexConstructService.register(mappedFile);
            } catch (IOException e) {
                throw new MessageFileCreateException();
            }
        }
    }

    /**
     * 构建消息文件路径
     *
     * @param topic 主题
     * @param partitionId 分区id
     * @return {@link String}
     */
    private String buildMessageFilePath(final String topic, final int partitionId) {
        return MESSAGE_STORE_BASE_PATH + File.separator + topic + File.separator + partitionId;
    }

    /**
     * 写入文件
     *
     * @param file 文件
     * @param data 数据
     * @return boolean
     */
    private void write(final MappedFile file, final byte[] data) {
        final boolean isWriteSuccess = file.put(data);
        if (!isWriteSuccess) {
            throw new MessageWriteFailedException();
        }
    }

    /**
     * 提交消息刷盘任务
     *
     * @param file 文件
     */
    private void submitMessageFlushJob(final MappedFile file) {
        // 提交刷盘任务
        final MessageFlushJob job = buildMessageFlushJob(file);
        this.messageFileFlushThreadService.submit(job);

        // 刷盘等待
        waitMessageFlushJobResultIfNecessary(job);
    }

    /**
     * 如果有必要则等待等待消息刷盘结果
     *
     * @param job 工作
     */
    private void waitMessageFlushJobResultIfNecessary(final MessageFlushJob job) {
        if (BrokerConstant.MESSAGE_FLUSH_POLICY == MessageFlushPolicyEnum.SYNC) {
            MessageFlushResultEnum result = MessageFlushResultEnum.FAILED;
            try {
                result = job.getFuture().get(BrokerConstant.MESSAGE_FLUSH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
                // ignore
            }

            if (result != MessageFlushResultEnum.SUCCESS) {
                LOGGER.warn("刷盘任务执行超时. filename:[" + job.getFile().getFilename() + "]");
                throw new MessageFlushTimeoutException();
            }
        }
    }

    /**
     * 构建消息刷盘任务
     *
     * @param file 文件
     * @return {@link MessageFlushJob}
     */
    private MessageFlushJob buildMessageFlushJob(final MappedFile file) {
        return MessageFlushJob.builder().file(file).flushOffset(file.getLastWritePosition())
            .attemptTimes(BrokerConstant.MESSAGE_FLUSH_RETRY_TIMES).build();
    }

    /**
     * 文件刷盘
     *
     * @param file 文件
     * @return boolean
     */
    private boolean flush(final MappedFile file) {
        return file.flush();
    }

    /**
     * 根据Topic和PartitionId查询对应的MappedFile
     *
     * @param topic 主题
     * @param partitionId 分区id
     * @return {@link Optional}<{@link MappedFile}>
     */
    private Optional<MappedFile> tryToFindMappedFileByTopicPartition(final String topic, final int partitionId) {
        return Optional.ofNullable(mappedFileTable.get(topic))
            .map(mappedFileGroupByTopic -> mappedFileGroupByTopic.get(partitionId));
    }

    /**
     * 消息文件异步落盘线程服务
     *
     * @author yuanbo
     * @date 2022/09/21 14:45:36
     */
    private class MessageFileFlushThreadService extends AbstractThreadService {
        private static final String NAME = "messageFileFlushThreadService";
        private final BlockingDeque<MessageFlushJob> jobs = new LinkedBlockingDeque<>();

        public MessageFileFlushThreadService() {
            super(10L);
        }

        /**
         * @description: 提交刷盘任务
         * @param:
         * @return:
         * @date: 2022/10/06 17:03:26
         */
        public void submit(final MessageFlushJob job) {
            try {
                jobs.putLast(job);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // ignore
            }
        }

        @Override
        protected String getName() {
            return NAME;
        }

        @Override
        protected void execute() {
            try {
                final MessageFlushJob job = jobs.takeFirst();
                final MappedFile mappedFile = job.getFile();
                MessageFlushResultEnum result = MessageFlushResultEnum.FAILED;
                for (int i = 0; i < job.getAttemptTimes(); ++i) {
                    // 刷盘
                    if (flush(mappedFile)) {
                        result = MessageFlushResultEnum.SUCCESS;
                        break;
                    }
                }

                job.complete(result);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // ignore
            }
        }
    }
}
