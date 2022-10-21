package org.ybonfire.pipeline.broker.store.message;

import lombok.EqualsAndHashCode;
import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.exception.FileLoadException;
import org.ybonfire.pipeline.broker.exception.MessageFileIllegalStateException;
import org.ybonfire.pipeline.broker.model.store.Index;
import org.ybonfire.pipeline.broker.model.store.SelectMappedFileDataResult;
import org.ybonfire.pipeline.broker.store.file.MappedFile;
import org.ybonfire.pipeline.broker.store.index.IndexLog;
import org.ybonfire.pipeline.broker.store.index.impl.DefaultIndexStoreServiceImpl;
import org.ybonfire.pipeline.common.constant.CommonConstant;
import org.ybonfire.pipeline.common.model.Message;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息文件
 *
 * @author yuanbo
 * @date 2022-10-08 17:15
 */
@EqualsAndHashCode
public final class MessageLog {
    private static final String MESSAGE_STORE_BASE_PATH = BrokerConstant.BROKER_STORE_BASE_PATH + "message";
    private static final int MESSAGE_LOG_SIZE = 16 * 1024 * 1024; // 16MB
    private final Lock lock = new ReentrantLock();
    private final String topic;
    private final int partitionId;
    private final MappedFile file;
    private volatile MessageLogState state;

    private MessageLog(final String topic, final int partitionId) throws IOException {
        this.topic = topic;
        this.partitionId = partitionId;
        this.file =
            MappedFile.create(buildMessageLogFilenameByTopicPartition(this.topic, this.partitionId), MESSAGE_LOG_SIZE);
        this.state = MessageLogState.WORKING;
    }

    private MessageLog(final File file) throws IOException {
        final String[] parseResult = parseTopicPartitionByFilename(file.getAbsolutePath());
        final String topic = parseResult[0];
        final int partitionId = Integer.parseInt(parseResult[1]);

        final MappedFile mappedFile = MappedFile.from(file);

        this.topic = topic;
        this.partitionId = partitionId;
        this.file = mappedFile;
        this.state = MessageLogState.WORKING;
    }

    /**
     * 写入消息
     *
     * @param message 消息
     */
    public void put(final Message message) {
        if (message == null) {
            return;
        }
        acquireOK();

        lock.lock();
        try {
            // 二进制化
            final byte[] data = convert(message);
            file.put(data);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 消息刷盘
     *
     * @return boolean
     */
    public boolean flush() {
        acquireOK();

        lock.lock();
        try {
            return file.flush();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取文件从position位置开始的全部数据
     *
     * @param position 位置
     * @return {@link Optional}<{@link SelectMappedFileDataResult}>
     */
    public Optional<SelectMappedFileDataResult> get(final int position) {
        acquireOK();

        lock.lock();
        try {
            return file.get(position);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取消息文件从position位置开始的size大小的数据
     *
     * @param position 位置
     * @param size 大小
     * @return {@link Optional}<{@link SelectMappedFileDataResult}>
     */
    public Optional<SelectMappedFileDataResult> get(final int position, final int size) {
        acquireOK();

        lock.lock();
        try {
            return file.get(position, size);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @description: 根据索引信息查询消息
     * @param:
     * @return:
     * @date: 2022/10/21 11:39:42
     */
    public Optional<Message> select(final Index index) {
        acquireOK();

        lock.lock();
        try {
            final int position = index.getStartOffset();
            final int size = index.getSize();
            final Optional<SelectMappedFileDataResult> resultOptional = file.get(position, size);
            return resultOptional.map(SelectMappedFileDataResult::getData).map(this::convert);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 摧毁消息文件
     */
    public void destroy() {
        acquireOK();

        lock.lock();
        try {
            file.destroy();
            state = MessageLogState.DESTROYED;
        } catch (IOException ex) {
            // ignored
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取消息文件对应Topic
     *
     * @return {@link String}
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 获取消息文件对应PartitionId
     *
     * @return int
     */
    public int getPartitionId() {
        return partitionId;
    }

    /**
     * 获取文件名
     *
     * @return {@link String}
     */
    public String getFilename() {
        return file.getFilename();
    }

    /**
     * 获取写入偏移量
     *
     * @return int
     */
    public int getLastWritePosition() {
        return file.getLastWritePosition();
    }

    /**
     * 设置写入偏移量
     *
     * @param writePosition 写入偏移量
     */
    public void setLastWritePosition(final int writePosition) {
        file.setLastWritePosition(writePosition);
    }

    /**
     * 获取刷盘偏移量
     *
     * @return int
     */
    public int getLastFlushPosition() {
        return file.getLastFlushPosition();
    }

    /**
     * 设置刷盘偏移量
     *
     * @param flushPosition 刷盘偏移量
     */
    public void setLastFlushPosition(final int flushPosition) {
        file.setLastFlushPosition(flushPosition);
    }

    /**
     * 将消息转换为二进制数据，并添加数据长度、存储时间戳等信息
     *
     * @param message 消息
     * @return {@link byte[]}
     */
    private byte[] convert(final Message message) {
        // id
        final byte[] idBytes = message.getId().getBytes(CommonConstant.CHARSET_UTF8);
        final int idByteLength = idBytes.length;
        // payload
        final int payloadBytesLength = message.getPayload().length;
        // timestamp
        final long timestamp = System.currentTimeMillis();
        // total length
        final int totalLength = CommonConstant.INT_BYTE_LENGTH + idByteLength/*id*/ + CommonConstant.INT_BYTE_LENGTH
            + payloadBytesLength/*payload*/ + CommonConstant.LONG_BYTE_LENGTH/*timestamp*/;

        final ByteBuffer buffer = ByteBuffer.allocate(totalLength + CommonConstant.INT_BYTE_LENGTH);
        buffer.putInt(totalLength); // totalLength
        buffer.putInt(idByteLength); // id
        buffer.put(idBytes);
        buffer.putInt(payloadBytesLength); // payload
        buffer.put(message.getPayload());
        buffer.putLong(timestamp); // timestamp

        return buffer.array();
    }

    /**
     * 将二进制数据转换为消息
     *
     * @param buffer 二进制数据
     * @return {@link Message}
     */
    private Message convert(final ByteBuffer buffer) {
        final int totalLength = buffer.getInt();
        final int idLength = buffer.getInt();
        final byte[] idBytes = new byte[idLength];
        buffer.get(idBytes);
        final String id = new String(idBytes, CommonConstant.CHARSET_UTF8);
        final int payloadLength = buffer.get();
        final byte[] payloadBytes = new byte[payloadLength];
        buffer.get(payloadBytes);
        final long timestamp = buffer.getLong();

        return Message.builder().id(id).topic(topic).payload(payloadBytes).build();
    }

    /**
     * 根据Topic、Partition构建消息文件路径
     *
     * @param topic 主题
     * @param partitionId 分区id
     * @return {@link String}
     */
    private String buildMessageLogFilenameByTopicPartition(final String topic, final int partitionId) {
        return MESSAGE_STORE_BASE_PATH + File.separator + topic + File.separator + partitionId;
    }

    /**
     * 根据消息文件路径解析Topic、Partition
     *
     * @param filename 文件名
     * @return {@link String}
     */
    private String[] parseTopicPartitionByFilename(final String filename) {
        if (filename.startsWith(MESSAGE_STORE_BASE_PATH)) {
            final String subFilename = filename.substring((MESSAGE_STORE_BASE_PATH + File.separator).length());
            final String topic = subFilename.substring(0, subFilename.indexOf(File.separator));
            final String partition = subFilename.substring(subFilename.indexOf(File.separator) + 1);

            return new String[] {topic, partition};
        }

        throw new FileLoadException();
    }

    /**
     * @description: 确保MessageLog状态为Working
     * @param:
     * @return:
     * @date: 2022/10/15 10:44:57
     */
    private void acquireOK() {
        if (this.state != MessageLogState.WORKING) {
            throw new MessageFileIllegalStateException();
        }
    }

    /**
     * 创建MessageLog
     *
     * @param topic 主题
     * @param partitionId 分区id
     * @return {@link MessageLog}
     * @throws IOException ioexception
     */
    public static MessageLog create(final String topic, final int partitionId) throws IOException {
        return new MessageLog(topic, partitionId);
    }

    /**
     * 加载所有日志文件
     *
     * @return {@link List}<{@link MessageLog}>
     * @throws IOException ioexception
     */
    public static List<MessageLog> reloadAll() throws IOException {
        final List<MessageLog> logs = new ArrayList<>();

        final File messageLogDir = new File(MESSAGE_STORE_BASE_PATH);
        if (messageLogDir.isDirectory()) {
            final File[] messageLogFilesGroupByTopic = messageLogDir.listFiles();
            for (final File messageLogFilesGroup : messageLogFilesGroupByTopic) {
                if (messageLogFilesGroup.isDirectory()) {
                    final File[] messageLogFiles = messageLogFilesGroup.listFiles();
                    if (messageLogFiles != null) {
                        for (final File messageLogFile : messageLogFiles) {
                            final MessageLog log = new MessageLog(messageLogFile);
                            resetMessageLogPositions(log);
                            logs.add(log);
                        }
                    }
                }
            }
        }

        return logs;
    }

    /**
     * @description: 重置对应MessageLog的偏移量
     * @param:
     * @return:
     * @date: 2022/10/19 16:49:17
     */
    private static void resetMessageLogPositions(final MessageLog messageLog) {
        if (messageLog == null) {
            return;
        }

        int position = 0;
        final String topic = messageLog.getTopic();
        final int partitionId = messageLog.getPartitionId();

        // 读取对应Topic、Partition的IndexLog.
        final Optional<IndexLog> indexLogOptional =
            DefaultIndexStoreServiceImpl.getInstance().tryToFindIndexLogByTopicPartition(topic, partitionId);
        if (indexLogOptional.isPresent()) {
            final IndexLog indexLog = indexLogOptional.get();
            final Optional<SelectMappedFileDataResult> resultOptional = indexLog.get(position);
            if (resultOptional.isPresent()) {
                final SelectMappedFileDataResult result = resultOptional.get();
                final ByteBuffer byteBuffer = result.getData();

                // 获取最后一条Index，计算MessageLog偏移量
                byteBuffer.position(byteBuffer.limit() - IndexLog.INDEX_UNIT_BYTE_LENGTH);
                position = byteBuffer.getInt();
                position += byteBuffer.getInt();
            }
        }

        // 设置MessageLog偏移量
        messageLog.setLastFlushPosition(position);
        messageLog.setLastWritePosition(position);
    }

    /**
     * @description: MessageLog状态
     * @author: yuanbo
     * @date: 2022/10/15
     */
    private enum MessageLogState {
        /**
         * 工作中
         */
        WORKING,
        /**
         * 已销毁
         */
        DESTROYED
    }
}
