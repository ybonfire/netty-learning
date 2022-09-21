package org.ybonfire.pipeline.broker.store.message.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.ybonfire.pipeline.broker.exception.MessageFileNotEnoughSpaceException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;

/**
 * 文件对象
 *
 * @author yuanbo
 * @date 2022-09-21 09:44
 */
public class MappedFile {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final int FILE_SIZE = 16 * 1024 * 1024; // 16MB
    private final ReentrantLock lock = new ReentrantLock();
    private final String filename;
    private final File file;
    private FileChannel channel;
    private MappedByteBuffer mappedByteBuffer;
    private volatile long lastWriteTimestamp = -1L;
    private volatile AtomicInteger lastWritePosition = new AtomicInteger(0);

    public void put(final byte[] data) {
        lock.lock();
        try {
            // 判断文件空间
            if (lastWritePosition.get() + data.length > FILE_SIZE) {
                throw new MessageFileNotEnoughSpaceException();
            }

            mappedByteBuffer.put(data);
            flush();
            lastWritePosition.addAndGet(data.length);
            lastWriteTimestamp = System.currentTimeMillis();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 刷盘
     */
    public void flush() {
        mappedByteBuffer.force();
    }

    private MappedFile(final String filename) throws IOException {
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

    /**
     * 创建MappedFile
     *
     * @param filename 文件名
     * @return {@link MappedFile}
     * @throws IOException ioexception
     */
    public static MappedFile create(final String filename) throws IOException {
        return new MappedFile(filename);
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

    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("user.home") );
    }
}
