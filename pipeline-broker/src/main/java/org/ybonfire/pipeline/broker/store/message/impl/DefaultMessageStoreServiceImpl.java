package org.ybonfire.pipeline.broker.store.message.impl;

import org.apache.commons.collections4.MapUtils;
import org.ybonfire.pipeline.broker.callback.ITopicConfigUpdateEventCallback;
import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.exception.FileLoadException;
import org.ybonfire.pipeline.broker.exception.MessageFileCreateException;
import org.ybonfire.pipeline.broker.model.store.MessageFlushPolicyEnum;
import org.ybonfire.pipeline.broker.model.store.MessageFlushResultEnum;
import org.ybonfire.pipeline.broker.model.store.MessageLogFlushJob;
import org.ybonfire.pipeline.broker.model.topic.PartitionConfig;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.broker.model.topic.TopicConfigUpdateEvent;
import org.ybonfire.pipeline.broker.store.file.MappedFile;
import org.ybonfire.pipeline.broker.store.index.impl.DefaultIndexStoreServiceImpl;
import org.ybonfire.pipeline.broker.store.message.IMessageStoreService;
import org.ybonfire.pipeline.broker.store.message.MessageLog;
import org.ybonfire.pipeline.broker.topic.impl.DefaultTopicConfigManager;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;
import org.ybonfire.pipeline.server.exception.MessageFlushTimeoutException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 消息存储服务
 *
 * @author yuanbo
 * @date 2022-09-14 18:30
 */
public class DefaultMessageStoreServiceImpl implements IMessageStoreService {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final DefaultMessageStoreServiceImpl INSTANCE = new DefaultMessageStoreServiceImpl();
    private final Map<String/*topic+partitionId*/, Object> lockTable = new ConcurrentHashMap<>();
    private final Map<String/*topic*/, Map<Integer/*partitionId*/, MessageLog>> messageLogTable = new HashMap<>();
    private final MessageLogFlushThreadService messageLogFlushThreadService = new MessageLogFlushThreadService();
    private final ITopicConfigUpdateEventCallback callback = new DefaultTopicConfigUpdateCallback();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    private DefaultMessageStoreServiceImpl() {}

    /**
     * @description: 启动消息存储服务
     * @param:
     * @return:
     * @date: 2022/09/21 14:47:02
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            onStart();
        }
    }

    /**
     * @description: 判断消息存储服务是否启动
     * @param:
     * @return:
     * @date: 2022/09/21 14:47:02
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭消息存储服务
     * @param:
     * @return:
     * @date: 2022/09/21 14:47:02
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(false, true)) {
            onShutdown();
        }
    }

    /**
     * 尝试根据Topic和PartitionId查询对应的MessageLog
     *
     * @param topic 主题
     * @param partitionId 分区id
     * @return {@link Optional}<{@link MappedFile}>
     */
    @Override
    public Optional<MessageLog> tryToFindMessageLogByTopicPartition(final String topic, final int partitionId) {
        return Optional.ofNullable(messageLogTable.get(topic))
            .map(messageLogGroupByTopic -> messageLogGroupByTopic.get(partitionId));
    }

    /**
     * @description: 存储消息
     * @param:
     * @return:
     * @date: 2022/09/14 18:30:19
     */
    @Override
    public void store(final String topic, final int partitionId, final Message message) {
        // 确保服务已启动
        acquireOK();

        // 确保文件已创建完毕
        ensureMessageLogCreateOK(topic, partitionId);

        // 查询指定TopicPartition的消息文件
        final Optional<MessageLog> messageLogOptional = tryToFindMessageLogByTopicPartition(topic, partitionId);
        if (messageLogOptional.isPresent()) {
            final MessageLog messageLog = messageLogOptional.get();

            // 写入数据
            write(messageLog, message);

            // 提交刷盘任务
            submitMessageFlushJob(messageLog);
        }
    }

    /**
     * @description: 重新加载消息文件数据
     * @param:
     * @return:
     * @date: 2022/10/11 16:38:05
     */
    @Override
    public synchronized void reload() {
        try {
            final List<MessageLog> messageLogs = MessageLog.reloadAll();
            for (final MessageLog messageLog : messageLogs) {
                final String topic = messageLog.getTopic();
                final int partitionId = messageLog.getPartitionId();

                final Map<Integer, MessageLog> messageLogGroupByTopic =
                    messageLogTable.computeIfAbsent(topic, key -> new HashMap<>());
                if (!messageLogGroupByTopic.containsKey(partitionId)) {
                    messageLogGroupByTopic.put(partitionId, messageLog);
                    // 注册IndexConstructWorker
                    DefaultIndexStoreServiceImpl.getInstance().register(topic, partitionId);
                }
            }
        } catch (IOException ex) {
            throw new FileLoadException();
        }
    }

    /**
     * 确保MessageLog已创建好文件
     *
     * @param topic 主题
     * @param partitionId 分区id
     */
    private void ensureMessageLogCreateOK(final String topic, final int partitionId) {
        final Object lockObject = lockTable.computeIfAbsent(topic + partitionId, key -> new Object());

        synchronized (lockObject) {
            final Map<Integer, MessageLog> messageLogGroupByTopic =
                messageLogTable.computeIfAbsent(topic, key -> new HashMap<>());
            if (!messageLogGroupByTopic.containsKey(partitionId)) {
                try {
                    final MessageLog messageLog = MessageLog.create(topic, partitionId);
                    messageLogGroupByTopic.put(partitionId, messageLog);
                    DefaultIndexStoreServiceImpl.getInstance().register(topic, partitionId);
                } catch (IOException e) {
                    throw new MessageFileCreateException();
                }
            }
        }
    }

    /**
     * 写入文件
     *
     * @param messageLog 消息文件
     * @param message 消息
     */
    private void write(final MessageLog messageLog, final Message message) {
        messageLog.put(message);
    }

    /**
     * 提交消息刷盘任务
     *
     * @param messageLog 消息文件
     */
    private void submitMessageFlushJob(final MessageLog messageLog) {
        // 提交刷盘任务
        final MessageLogFlushJob job = buildMessageFlushJob(messageLog);
        this.messageLogFlushThreadService.submit(job);

        // 刷盘等待
        waitMessageFlushJobResultIfNecessary(job);
    }

    /**
     * 如果有必要则等待等待消息刷盘结果
     *
     * @param job 工作
     */
    private void waitMessageFlushJobResultIfNecessary(final MessageLogFlushJob job) {
        if (BrokerConstant.MESSAGE_FLUSH_POLICY == MessageFlushPolicyEnum.SYNC) {
            MessageFlushResultEnum result = MessageFlushResultEnum.FAILED;
            try {
                result = job.getFuture().get(BrokerConstant.MESSAGE_FLUSH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
                // ignore
            }

            if (result != MessageFlushResultEnum.SUCCESS) {
                LOGGER.warn("刷盘任务执行超时. filename:[" + job.getMessageLog().getFilename() + "]");
                throw new MessageFlushTimeoutException();
            }
        }
    }

    /**
     * 构建消息刷盘任务
     *
     * @param messageLog 消息文件
     * @return {@link MessageLogFlushJob}
     */
    private MessageLogFlushJob buildMessageFlushJob(final MessageLog messageLog) {
        return MessageLogFlushJob.builder().messageLog(messageLog).build();
    }

    /**
     * @description: 确保服务已就绪
     * @param:
     * @return:
     * @date: 2022/05/19 11:49:04
     */
    private void acquireOK() {
        if (!this.isStarted.get()) {
            throw new LifeCycleException();
        }
    }

    /**
     * @description: 服务启动流程
     * @param:
     * @return:
     * @date: 2022/10/13 10:10:00
     */
    private void onStart() {
        // 开启索引构建服务
        DefaultIndexStoreServiceImpl.getInstance().start();
        // 加载消息文件数据
        reload();
        // 注册Topic配置变更回调
        DefaultTopicConfigManager.getInstance().register(callback);
        // 开启刷盘线程
        messageLogFlushThreadService.start();
    }

    /**
     * @description: 服务关闭流程
     * @param:
     * @return:
     * @date: 2022/10/13 10:09:17
     */
    private void onShutdown() {
        // 关闭刷盘线程
        messageLogFlushThreadService.stop();
        // 取消注册Topic配置变更回调
        DefaultTopicConfigManager.getInstance().deregister(callback);
        // 消息文件数据刷盘
        flushAll();
        // 关闭索引构建服务
        DefaultIndexStoreServiceImpl.getInstance().shutdown();
    }

    /**
     * @description: 对所有消息文件进行刷盘
     * @param:
     * @return:
     * @date: 2022/10/13 10:11:47
     */
    private void flushAll() {
        messageLogTable.values().parallelStream().flatMap(logsGroupByTopic -> logsGroupByTopic.values().stream())
            .forEach(MessageLog::flush);
    }

    /**
     * 获取DefaultMessageStoreServiceImpl实例
     *
     * @return {@link DefaultMessageStoreServiceImpl}
     */
    public static DefaultMessageStoreServiceImpl getInstance() {
        return INSTANCE;
    }

    /**
     * 消息文件异步落盘线程服务
     *
     * @author yuanbo
     * @date 2022/09/21 14:45:36
     */
    private static class MessageLogFlushThreadService extends AbstractThreadService {
        private static final String NAME = "messageFileFlushThreadService";
        private final BlockingDeque<MessageLogFlushJob> jobs = new LinkedBlockingDeque<>();

        public MessageLogFlushThreadService() {
            super(10L);
        }

        /**
         * @description: 提交刷盘任务
         * @param:
         * @return:
         * @date: 2022/10/06 17:03:26
         */
        public void submit(final MessageLogFlushJob job) {
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
                final MessageLogFlushJob job = jobs.takeFirst();
                final MessageLog messageLog = job.getMessageLog();
                MessageFlushResultEnum result = MessageFlushResultEnum.FAILED;
                for (int i = 0; i < BrokerConstant.MESSAGE_FLUSH_RETRY_TIMES; ++i) {
                    // 刷盘
                    if (messageLog.flush()) {
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

    /**
     * 默认TopicConfig更新事件回调
     *
     * @author yuanbo
     * @date 2022-10-14 16:28
     */
    private class DefaultTopicConfigUpdateCallback implements ITopicConfigUpdateEventCallback {

        private DefaultTopicConfigUpdateCallback() {}

        /**
         * @description: 更新事件回调
         * @param:
         * @return:
         * @date: 2022/10/14 13:59:57
         */
        @Override
        public void onEvent(final TopicConfigUpdateEvent event) {
            switch (event.getType()) {
                case ADD:
                    onAddEvent(event);
                    break;
                case UPDATE:
                    onUpdateEvent(event);
                    break;
                case DELETE:
                    onDeleteEvent(event);
                    break;
                default:
                    break;
            }
        }

        /**
         * @description: TopicConfig新增事件处理
         * @param:
         * @return:
         * @date: 2022/10/14 16:30:30
         */
        private void onAddEvent(final TopicConfigUpdateEvent event) {
            // no ops
        }

        /**
         * @description: TopicConfig更新事件处理
         * @param:
         * @return:
         * @date: 2022/10/14 16:30:32
         */
        private void onUpdateEvent(final TopicConfigUpdateEvent event) {
            if (event == null) {
                return;
            }

            final TopicConfig config = event.getConfig();
            if (config == null) {
                return;
            }

            final Set<Integer> partitionIds =
                config.getPartitions().stream().map(PartitionConfig::getPartitionId).collect(Collectors.toSet());
            final Map<Integer, MessageLog> messageLogGroupByTopic =
                MapUtils.emptyIfNull(messageLogTable.get(config.getTopic()));
            // 销毁该Topic下所有的不属于TopicConfig的Partition下的消息文件
            final Iterator<Map.Entry<Integer, MessageLog>> iter = messageLogGroupByTopic.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry<Integer, MessageLog> entry = iter.next();
                final int partitionId = entry.getKey();
                final MessageLog messageLog = entry.getValue();
                if (!partitionIds.contains(partitionId)) {
                    // 删除对应Partition的消息文件
                    messageLog.destroy();
                    iter.remove();
                }
            }
        }

        /**
         * @description: TopicConfig删除事件处理
         * @param:
         * @return:
         * @date: 2022/10/14 16:30:34
         */
        private void onDeleteEvent(final TopicConfigUpdateEvent event) {
            if (event == null) {
                return;
            }

            final String topic = event.getTopic();

            // 销毁该Topic下所有Partition的消息文件
            MapUtils.emptyIfNull(messageLogTable.get(topic)).values().parallelStream().forEach(MessageLog::destroy);
            messageLogTable.remove(topic);
        }
    }
}