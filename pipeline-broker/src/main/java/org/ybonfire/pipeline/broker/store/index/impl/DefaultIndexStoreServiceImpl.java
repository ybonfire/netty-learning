package org.ybonfire.pipeline.broker.store.index.impl;

import org.apache.commons.lang3.StringUtils;
import org.ybonfire.pipeline.broker.exception.FileLoadException;
import org.ybonfire.pipeline.broker.exception.IndexFileCreateException;
import org.ybonfire.pipeline.broker.model.store.Index;
import org.ybonfire.pipeline.broker.model.store.SelectMappedFileDataResult;
import org.ybonfire.pipeline.broker.store.index.IIndexStoreService;
import org.ybonfire.pipeline.broker.store.index.IndexLog;
import org.ybonfire.pipeline.broker.store.message.MessageLog;
import org.ybonfire.pipeline.broker.store.message.impl.DefaultMessageStoreServiceImpl;
import org.ybonfire.pipeline.common.constant.CommonConstant;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;
import org.ybonfire.pipeline.server.exception.UnknownException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息索引存储服务
 *
 * @author yuanbo
 * @date 2022-10-07 10:50
 */
public final class DefaultIndexStoreServiceImpl implements IIndexStoreService {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final IIndexStoreService INSTANCE = new DefaultIndexStoreServiceImpl();
    private final Map<String, Map<Integer, IndexLog>> indexTable = new HashMap<>();
    private final CopyOnWriteArraySet<IndexConstructWorker> workers = new CopyOnWriteArraySet<>();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    private DefaultIndexStoreServiceImpl() {}

    /**
     * @description: 启动消息索引构建服务
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:48
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            onStart();
        }
    }

    /**
     * @description: 判断索引存储服务是否启动
     * @param:
     * @return:
     * @date: 2022/09/21 14:47:02
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭消息索引构建服务
     * @param:
     * @return:
     * @date: 2022/10/11 16:37:01
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            onShutdown();
        }
    }

    /**
     * @description: 注册指定Topic、Partition的索引构建Worker
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:54
     */
    @Override
    public synchronized void register(final String topic, final int partitionId) {
        // 确保服务已启动
        acquireOK();

        // 查询指定Topic、Partition的消息文件
        final Optional<MessageLog> optional =
            DefaultMessageStoreServiceImpl.getInstance().tryToFindMessageLogByTopicPartition(topic, partitionId);
        if (optional.isPresent()) {
            // 确保IndexLog文件已创建
            ensureIndexLogCreateOK(topic, partitionId);

            // 创建IndexConstructWorker
            final IndexConstructWorker worker = new IndexConstructWorker(topic, partitionId);
            workers.add(worker);
            worker.start();
        } else {
            LOGGER.error(
                "注册失败. 未查询到指定Topic、Partition对应的消息文件. topic:[" + topic + "]" + "| partition:[" + partitionId + "]");
        }
    }

    /**
     * @description: 取消注册指定Topic、Partition的索引构建Worker
     * @param:
     * @return:
     * @date: 2022/10/13 18:12:18
     */
    @Override
    public synchronized void deregister(final String topic, final int partitionId) {
        // 确保服务已启动
        acquireOK();

        // 查询指定Topic、Partition的消息文件
        final Optional<MessageLog> indexLogOptional =
            DefaultMessageStoreServiceImpl.getInstance().tryToFindMessageLogByTopicPartition(topic, partitionId);
        if (!indexLogOptional.isPresent()) {
            // 查询指定Topic、Partition的索引构建Worker
            final Optional<IndexConstructWorker> workerOptional =
                tryToFindIndexLogConstructWorkerByTopicPartition(topic, partitionId);
            if (workerOptional.isPresent()) {
                // 停止并移除对应Worker
                final IndexConstructWorker worker = workerOptional.get();
                worker.stop();
                workers.remove(worker);
            }
        } else {
            LOGGER.error(
                "取消注册失败. 查询到指定Topic、Partition对应的消息文件. topic:[" + topic + "]" + " | partition:[" + partitionId + "]");
        }
    }

    /**
     * @description: 尝试根据Topic、Partition获取索引文件
     * @param:
     * @return:
     * @date: 2022/10/13 17:33:50
     */
    @Override
    public Optional<IndexLog> tryToFindIndexLogByTopicPartition(String topic, int partitionId) {
        return Optional.ofNullable(indexTable.get(topic))
            .map(indexLogGroupByTopic -> indexLogGroupByTopic.get(partitionId));
    }

    /**
     * @description: 尝试根据Topic、Partition获取索引构建Worker
     * @param:
     * @return:
     * @date: 2022/10/13 18:18:55
     */
    private Optional<IndexConstructWorker> tryToFindIndexLogConstructWorkerByTopicPartition(final String topic,
        final int partitionId) {
        for (final IndexConstructWorker worker : workers) {
            if (StringUtils.equals(worker.getTopic(), topic) && worker.getPartitionId() == partitionId) {
                return Optional.of(worker);
            }
        }

        return Optional.empty();
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
     * @description: 重新加载文件数据
     * @param:
     * @return:
     * @date: 2022/10/11 16:38:05
     */
    @Override
    public synchronized void reload() {
        try {
            final List<IndexLog> indexLogs = IndexLog.reloadAll();
            for (final IndexLog indexLog : indexLogs) {
                final String topic = indexLog.getTopic();
                final int partitionId = indexLog.getPartitionId();

                if (!indexTable.containsKey(topic)) {
                    indexTable.put(topic, new HashMap<>());
                }

                final Map<Integer, IndexLog> indexLogGroupByTopic = indexTable.get(topic);
                if (!indexLogGroupByTopic.containsKey(partitionId)) {
                    indexLogGroupByTopic.put(partitionId, indexLog);
                }
            }
        } catch (IOException ex) {
            throw new FileLoadException();
        }
    }

    /**
     * 确保IndexLog已创建好文件
     *
     * @param topic 主题
     * @param partitionId 分区id
     */
    private synchronized void ensureIndexLogCreateOK(final String topic, final int partitionId) {
        if (!indexTable.containsKey(topic)) {
            indexTable.put(topic, new HashMap<>());
        }

        final Map<Integer, IndexLog> indexLogGroupByTopic = indexTable.get(topic);
        if (!indexLogGroupByTopic.containsKey(partitionId)) {
            try {
                final IndexLog indexLog = IndexLog.create(topic, partitionId);
                indexLogGroupByTopic.put(partitionId, indexLog);
            } catch (IOException e) {
                throw new IndexFileCreateException();
            }
        }
    }

    /**
     * @description: 服务启动流程
     * @param:
     * @return:
     * @date: 2022/10/13 10:10:00
     */
    private void onStart() {
        reload();
        workers.parallelStream().forEach(IndexConstructWorker::start);
    }

    /**
     * @description: 服务关闭流程
     * @param:
     * @return:
     * @date: 2022/10/13 10:09:17
     */
    private void onShutdown() {
        workers.parallelStream().forEach(IndexConstructWorker::stop);
        flushAll();
    }

    /**
     * @description: 对所有索引文件进行刷盘
     * @param:
     * @return:
     * @date: 2022/10/13 10:11:47
     */
    private void flushAll() {
        indexTable.values().parallelStream().flatMap(logsGroupByTopic -> logsGroupByTopic.values().stream())
            .forEach(IndexLog::flush);
    }

    /**
     * 获取DefaultMessageIndexStoreServiceImpl实例
     *
     * @return {@link IIndexStoreService}
     */
    public static IIndexStoreService getInstance() {
        return INSTANCE;
    }

    /**
     * @description: 消息索引构建工作线程
     * @author: yuanbo
     * @date: 2022/10/7
     */
    private class IndexConstructWorker extends AbstractThreadService {
        private static final String NAME = "messageIndexConstructWorker";
        private final IInternalLogger LOGGER = new SimpleInternalLogger();
        private final String topic;
        private final int partitionId;
        private final AtomicInteger lastIndexPosition = new AtomicInteger(0);

        public IndexConstructWorker(final String topic, final int partitionId) {
            super(100L);
            this.topic = topic;
            this.partitionId = partitionId;
        }

        @Override
        protected String getName() {
            return NAME;
        }

        @Override
        protected void execute() {
            final Optional<MessageLog> optional =
                DefaultMessageStoreServiceImpl.getInstance().tryToFindMessageLogByTopicPartition(topic, partitionId);
            if (optional.isPresent()) {
                final MessageLog messageLog = optional.get();

                // 确保索引文件已创建
                ensureIndexLogCreateOK(topic, partitionId);

                // 构建索引
                index(messageLog);
            } else {
                LOGGER.warn(
                    "IndexConstructWorker执行异常. 未找到对应的消息文件. topic:[" + topic + "] | partition:[" + partitionId + "]");
                deregister(topic, partitionId);
            }
        }

        private String getTopic() {
            return topic;
        }

        private int getPartitionId() {
            return partitionId;
        }

        /**
         * 构建消息索引
         *
         * @param messageLog 消息文件
         */
        private void index(final MessageLog messageLog) {
            if (messageLog == null) {
                return;
            }

            final IndexLog indexLog =
                tryToFindIndexLogByTopicPartition(topic, partitionId).orElseThrow(UnknownException::new);
            final int lastFlushPosition = messageLog.getLastFlushPosition();
            if (lastIndexPosition.get() == lastFlushPosition) {
                return;
            } else if (lastIndexPosition.get() > lastFlushPosition) {
                lastIndexPosition.set(lastFlushPosition);
                LOGGER.warn("MessageLog索引构建异常. 索引构建偏移量超过刷盘偏移量. messageLog:[" + messageLog + "]");
                return;
            }

            // 查询数据
            final Optional<SelectMappedFileDataResult> optional = messageLog.get(lastFlushPosition);
            if (optional.isPresent()) {
                final SelectMappedFileDataResult result = optional.get();

                // 构建索引
                lastIndexPosition.set(result.getStartPosition());

                final ByteBuffer data = result.getData();
                final int msgLength = data.getInt();
                data.position(lastFlushPosition + CommonConstant.INT_BYTE_LENGTH + msgLength);
                final long timestamp = data.getLong();

                final Index index =
                    Index.builder().startOffset(lastIndexPosition.get()).size(msgLength).timestamp(timestamp).build();
                indexLog.put(index);
                indexLog.flush();
                lastIndexPosition.addAndGet(result.getSize());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IndexConstructWorker)) {
                return false;
            }
            IndexConstructWorker that = (IndexConstructWorker)o;
            return partitionId == that.partitionId && topic.equals(that.topic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(topic, partitionId);
        }
    }
}
