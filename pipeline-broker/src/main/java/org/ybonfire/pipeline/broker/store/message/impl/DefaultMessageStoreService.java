package org.ybonfire.pipeline.broker.store.message.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ybonfire.pipeline.broker.exception.MessageFileCreateException;
import org.ybonfire.pipeline.broker.store.message.IMessageStoreService;
import org.ybonfire.pipeline.broker.store.message.file.MappedFile;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;

/**
 * 消息存储服务
 *
 * @author yuanbo
 * @date 2022-09-14 18:30
 */
public class DefaultMessageStoreService implements IMessageStoreService {
    private static final String MESSAGE_STORE_BASE_PATH =
        System.getProperty("user.home") + File.separator + "pipeline" + File.separator + "message";
    private final Map<String, Map<Integer, MappedFile>> mappedFileTable = new HashMap<>();
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
        ensureMappedFileTableOK(topic, partitionId);
        final MappedFile file = mappedFileTable.get(topic).get(partitionId);
        file.put(data);
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
                final MappedFile mappedFile = MappedFile.create(filename);
                mappedFileGroupByTopic.put(partitionId, mappedFile);
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
     * 消息文件异步落盘线程服务
     *
     * @author yuanbo
     * @date 2022/09/21 14:45:36
     */
    private class MessageFileFlushThreadService extends AbstractThreadService {
        private static final String NAME = "messageFileFlushThreadService";

        public MessageFileFlushThreadService() {
            super(200L);
        }

        @Override
        protected String getName() {
            return NAME;
        }

        @Override
        protected void execute() {
            for (final Map<Integer, MappedFile> mappedFileGroupByTopic : mappedFileTable.values()) {
                for (final MappedFile mappedFile : mappedFileGroupByTopic.values()) {
                    mappedFile.flush();
                }
            }
        }
    }
}
