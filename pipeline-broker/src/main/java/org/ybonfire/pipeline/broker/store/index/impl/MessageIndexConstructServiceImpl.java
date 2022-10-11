package org.ybonfire.pipeline.broker.store.index.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ybonfire.pipeline.broker.exception.IndexFileCreateException;
import org.ybonfire.pipeline.broker.model.Index;
import org.ybonfire.pipeline.broker.model.SelectMappedFileDataResult;
import org.ybonfire.pipeline.broker.store.index.IMessageIndexConstructService;
import org.ybonfire.pipeline.broker.store.index.IndexLog;
import org.ybonfire.pipeline.broker.store.message.MessageLog;
import org.ybonfire.pipeline.common.constant.CommonConstant;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;

/**
 * 消息索引创建服务
 *
 * @author yuanbo
 * @date 2022-10-07 10:50
 */
public final class MessageIndexConstructServiceImpl implements IMessageIndexConstructService {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final IMessageIndexConstructService INSTANCE = new MessageIndexConstructServiceImpl();
    private final Map<MessageLog, MessageIndexConstructWorker> workerTable = new ConcurrentHashMap<>();
    private final Map<MessageLog, IndexLog> indexTable = new ConcurrentHashMap<>();
    private final AtomicBoolean started = new AtomicBoolean(false);

    private MessageIndexConstructServiceImpl() {}

    /**
     * @description: 启动消息索引构建服务
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:48
     */
    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            for (final MessageIndexConstructWorker worker : workerTable.values()) {
                worker.start();
            }
        }
    }

    /**
     * @description: 注册消息文件对象
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:54
     */
    @Override
    public synchronized void register(final MessageLog messageLog) {
        if (messageLog == null) {
            return;
        }

        // 确保服务已启动
        acquireOK();

        // 确保IndexLog文件已创建
        ensureIndexLogCreateOK(messageLog);

        final MessageIndexConstructWorker newWorker = new MessageIndexConstructWorker(messageLog);
        final MessageIndexConstructWorker prev = workerTable.putIfAbsent(messageLog, newWorker);
        if (prev == null) {
            newWorker.start();
        }
    }

    /**
     * @description: 停止消息索引构建服务
     * @param:
     * @return:
     * @date: 2022/10/11 16:37:01
     */
    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            for (final MessageIndexConstructWorker worker : workerTable.values()) {
                worker.stop();
            }
        }
    }

    /**
     * @description: 判断服务是否启动
     * @param:
     * @return:
     * @date: 2022/05/19 11:49:04
     */
    private void acquireOK() {
        if (!this.started.get()) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 确保IndexLog已创建好文件
     *
     * @param messageLog 消息文件
     */
    private synchronized void ensureIndexLogCreateOK(final MessageLog messageLog) {
        if (!indexTable.containsKey(messageLog)) {
            final String topic = messageLog.getTopic();
            final int partitionId = messageLog.getPartitionId();
            try {
                final IndexLog indexLog = IndexLog.create(topic, partitionId);
                indexTable.put(messageLog, indexLog);
            } catch (IOException ex) {
                throw new IndexFileCreateException();
            }
        }
    }

    /**
     * 获取MessageIndexConstructServiceImpl实例
     *
     * @return {@link IMessageIndexConstructService}
     */
    public static IMessageIndexConstructService getInstance() {
        return INSTANCE;
    }

    /**
     * @description: 消息索引构建工作线程
     * @author: yuanbo
     * @date: 2022/10/7
     */
    private class MessageIndexConstructWorker extends AbstractThreadService {
        private static final String NAME = "messageIndexConstructWorker";
        private final IInternalLogger LOGGER = new SimpleInternalLogger();
        private final MessageLog messageLog;
        private final AtomicInteger lastIndexPosition = new AtomicInteger(0);

        public MessageIndexConstructWorker(final MessageLog messageLog) {
            super(100L);
            this.messageLog = messageLog;
        }

        @Override
        protected String getName() {
            return NAME;
        }

        @Override
        protected void execute() {
            index(messageLog);
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

            // 确保IndexLog文件已创建
            ensureIndexLogCreateOK(messageLog);

            final IndexLog indexLog = findIndexLogByMessageLog(messageLog);
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

                for (int i = 0; i < result.getSize();) {
                    final int msgLength = data.getInt();
                    data.position(lastFlushPosition + CommonConstant.INT_BYTE_LENGTH + msgLength);
                    final long timestamp = data.getLong();

                    final Index index = Index.builder().startOffset(lastIndexPosition.get()).size(msgLength)
                        .timestamp(timestamp).build();
                    indexLog.put(index);
                    indexLog.flush();
                    lastIndexPosition.addAndGet(result.getSize());
                }
            }
        }

        /**
         * 根据MessageLog获取对应的IndexLog
         *
         * @param messageLog 消息日志
         * @return {@link Optional}<{@link IndexLog}>
         */
        private IndexLog findIndexLogByMessageLog(final MessageLog messageLog) {
            return indexTable.get(messageLog);
        }
    }
}
