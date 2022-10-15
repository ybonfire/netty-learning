package org.ybonfire.pipeline.broker.store.message;

import lombok.EqualsAndHashCode;
import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.exception.FileLoadException;
import org.ybonfire.pipeline.broker.exception.MessageFileIllegalStateException;
import org.ybonfire.pipeline.broker.model.store.SelectMappedFileDataResult;
import org.ybonfire.pipeline.broker.store.file.MappedFile;
import org.ybonfire.pipeline.common.constant.CommonConstant;
import org.ybonfire.pipeline.common.model.Message;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
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
        final String[] parseResult = parseTopicPartitionByFilename(file.getName());
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
     * 获取刷盘偏移量
     *
     * @return int
     */
    public int getLastFlushPosition() {
        return file.getLastFlushPosition();
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
        String[] result;
        if (filename.startsWith(MESSAGE_STORE_BASE_PATH)) {
            result = filename.substring((MESSAGE_STORE_BASE_PATH + File.separator).length()).split(File.separator);
        } else {
            result = filename.split(File.separator);
        }

        if (result.length == 2) {
            return result;
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
        final File messageLogDir = new File(MESSAGE_STORE_BASE_PATH);
        if (messageLogDir.isDirectory()) {
            final File[] messageLogFiles = messageLogDir.listFiles();
            if (messageLogFiles != null) {
                final List<MessageLog> logs = new ArrayList<>(messageLogFiles.length);
                for (final File messageLogFile : messageLogFiles) {
                    final MessageLog log = new MessageLog(messageLogFile);
                    // TODO set Positions
                    logs.add(log);
                }

                return logs;
            }
        }

        return Collections.emptyList();
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
