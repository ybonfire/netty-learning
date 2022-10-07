package org.ybonfire.pipeline.broker.store.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import lombok.EqualsAndHashCode;
import org.ybonfire.pipeline.broker.exception.MessageFileNotEnoughSpaceException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;

/**
 * 文件内存映射操作对象
 *
 * @author yuanbo
 * @date 2022-09-21 09:44
 */
@EqualsAndHashCode
public class MappedFile {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final int FILE_SIZE = 16 * 1024 * 1024; // 16MB
    private final String topic;
    private final int partitionId;
    private final String filename;
    private final File file;
    private FileChannel channel;
    private MappedByteBuffer mappedByteBuffer;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile long lastWriteTimestamp = -1L;
    private AtomicInteger lastWritePosition = new AtomicInteger(-1);
    private AtomicInteger lastFlushPosition = new AtomicInteger(-1);

    private MappedFile(final String topic, final int partitionId, final String filename) throws IOException {
        this.topic = topic;
        this.partitionId = partitionId;
        this.filename = filename;
        this.file = new File(this.filename);

        ensureDirOK(file.getParent());

        boolean ok = false;
        try {
            this.channel = new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.channel.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);
            ok = true;
        } finally {
            if (!ok && channel != null) {
                channel.close();
            }
        }
    }

    public boolean put(final byte[] data) {
        boolean success = false;
        lock.lock();
        try {
            // 判断文件空间
            if (lastWritePosition.get() + data.length > FILE_SIZE) {
                throw new MessageFileNotEnoughSpaceException();
            }

            mappedByteBuffer.put(data);
            lastWritePosition.addAndGet(data.length);
            lastWriteTimestamp = System.currentTimeMillis();
            success = true;
        } catch (BufferOverflowException | ReadOnlyBufferException ex) {
            LOGGER.warn("文件写入异常. filename:[" + filename + "]");
        } finally {
            lock.unlock();
        }

        return success;
    }

    /**
     * 刷盘
     */
    public boolean flush() {
        boolean success = false;
        lock.lock();
        try {
            mappedByteBuffer.force();
            lastFlushPosition.set(lastWritePosition.get());
            success = true;
        } catch (Exception ex) {
            LOGGER.warn("刷盘刷盘异常. filename:[" + filename + "]");
        } finally {
            lock.unlock();
        }

        return success;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public String getFilename() {
        return filename;
    }

    public int getLastWritePosition() {
        return lastWritePosition.get();
    }

    public int getLastFlushPosition() {
        return lastFlushPosition.get();
    }

    /**
     * @description: 确保文件夹已经创建
     * @param:
     * @return:
     * @date: 2020/9/29
     */
    private void ensureDirOK(final String dirName) {
        if (dirName != null) {
            File f = new File(dirName);
            if (!f.exists()) {
                boolean result = f.mkdirs();
                LOGGER.info(dirName + " mkdir " + (result ? "OK" : "Failed"));
            }
        }
    }

    /**
     * 创建MappedFile
     *
     * @param topic 主题
     * @param partitionId 分区id
     * @param filename 文件名
     * @return {@link MappedFile}
     * @throws IOException ioexception
     */
    public static MappedFile create(final String topic, final int partitionId, final String filename)
        throws IOException {
        return new MappedFile(topic, partitionId, filename);
    }
}
