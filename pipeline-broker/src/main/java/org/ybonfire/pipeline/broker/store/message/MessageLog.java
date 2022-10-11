package org.ybonfire.pipeline.broker.store.message;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.model.SelectMappedFileDataResult;
import org.ybonfire.pipeline.broker.store.file.MappedFile;
import org.ybonfire.pipeline.common.constant.CommonConstant;
import org.ybonfire.pipeline.common.model.Message;

import lombok.EqualsAndHashCode;

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

    private MessageLog(final String topic, final int partitionId) throws IOException {
        this.topic = topic;
        this.partitionId = partitionId;
        this.file = MappedFile.create(buildMessageLogFilename(this.topic, this.partitionId), MESSAGE_LOG_SIZE);
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
        lock.lock();
        try {
            return file.get(position, size);
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
     * 构建消息文件路径
     *
     * @param topic 主题
     * @param partitionId 分区id
     * @return {@link String}
     */
    private String buildMessageLogFilename(final String topic, final int partitionId) {
        return MESSAGE_STORE_BASE_PATH + File.separator + topic + File.separator + partitionId;
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
}
