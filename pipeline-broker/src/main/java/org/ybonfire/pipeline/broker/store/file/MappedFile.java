package org.ybonfire.pipeline.broker.store.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.ybonfire.pipeline.broker.exception.MessageFileNotEnoughSpaceException;
import org.ybonfire.pipeline.broker.model.SelectMappedFileDataResult;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.server.exception.MessageWriteFailedException;

import lombok.EqualsAndHashCode;

/**
 * 文件内存映射操作对象
 *
 * @author yuanbo
 * @date 2022-09-21 09:44
 */
@EqualsAndHashCode
public final class MappedFile {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final String filename;
    private final int fileSize;
    private final File file;
    private FileChannel channel;
    private final MappedByteBuffer mappedByteBuffer;
    private final AtomicInteger lastWritePosition = new AtomicInteger(0);
    private final AtomicInteger lastFlushPosition = new AtomicInteger(0);

    private MappedFile(final String filename, final int fileSize) throws IOException {
        this.filename = filename;
        this.fileSize = fileSize;
        this.file = new File(this.filename);

        ensureDirOK(file.getParent());

        boolean isSuccess = false;
        try {
            this.channel = new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.channel.map(FileChannel.MapMode.READ_WRITE, 0, this.fileSize);
            isSuccess = true;
        } finally {
            if (!isSuccess && channel != null) {
                channel.close();
            }
        }
    }

    /**
     * 写入数据
     *
     * @param data 数据
     */
    public void put(final byte[] data) {
        // 判断文件空间
        if (lastWritePosition.get() + data.length > fileSize) {
            throw new MessageFileNotEnoughSpaceException();
        }

        // 写入文件
        try {
            final ByteBuffer writeBuffer = mappedByteBuffer.slice();
            writeBuffer.position(lastWritePosition.get());
            writeBuffer.put(data);

            // 更新记录
            lastWritePosition.addAndGet(data.length);
        } catch (BufferOverflowException | ReadOnlyBufferException ex) {
            LOGGER.error("文件写入异常. filename:[" + filename + "]");
            throw new MessageWriteFailedException();
        }
    }

    /**
     * 消息刷盘
     * 
     * @return boolean
     */
    public boolean flush() {
        boolean isSuccess = false;
        try {
            mappedByteBuffer.force();
            lastFlushPosition.set(lastWritePosition.get());
            isSuccess = true;
        } catch (Exception ex) {
            LOGGER.warn("刷盘刷盘异常. filename:[" + filename + "]");
        }

        return isSuccess;
    }

    /**
     * 获取文件从position位置开始的全部数据
     *
     * @param position 位置
     * @return {@link Optional}<{@link SelectMappedFileDataResult}>
     */
    public Optional<SelectMappedFileDataResult> get(final int position) {
        final int flushPosition = getLastFlushPosition();
        final int size = flushPosition - position;
        if (size <= 0) {
            return Optional.empty();
        }

        final ByteBuffer readBuffer = mappedByteBuffer.slice();
        readBuffer.position(position);
        final ByteBuffer data = readBuffer.slice();
        data.limit(size);

        return Optional.of(SelectMappedFileDataResult.builder().startPosition(position).size(size).data(data).build());
    }

    /**
     * 获取文件从position位置开始的size大小的数据
     *
     * @param position 位置
     * @return {@link Optional}<{@link SelectMappedFileDataResult}>
     */
    public Optional<SelectMappedFileDataResult> get(final int position, final int size) {
        final ByteBuffer readBuffer = mappedByteBuffer.slice();
        readBuffer.position(position);
        final ByteBuffer data = readBuffer.slice();
        data.limit(size);

        return Optional.of(SelectMappedFileDataResult.builder().startPosition(position).size(size).data(data).build());
    }

    /**
     * 获取文件名
     *
     * @return {@link String}
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 获取写入偏移量
     *
     * @return int
     */
    public int getLastWritePosition() {
        return lastWritePosition.get();
    }

    /**
     * 获取刷盘偏移量
     *
     * @return int
     */
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
     * @param filename 文件名
     * @param fileSize 文件大小
     * @return {@link MappedFile}
     * @throws IOException ioexception
     */
    public static MappedFile create(final String filename, final int fileSize) throws IOException {
        return new MappedFile(filename, fileSize);
    }
}
