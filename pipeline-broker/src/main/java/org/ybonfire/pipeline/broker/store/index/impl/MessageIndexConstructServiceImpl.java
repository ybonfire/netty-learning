package org.ybonfire.pipeline.broker.store.index.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ybonfire.pipeline.broker.store.file.MappedFile;
import org.ybonfire.pipeline.broker.store.index.IMessageIndexConstructService;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;

/**
 * 消息索引创建服务
 *
 * @author yuanbo
 * @date 2022-10-07 10:50
 */
public class MessageIndexConstructServiceImpl implements IMessageIndexConstructService {
    private final Map<MappedFile, MessageIndexConstructWorker> workerTable = new ConcurrentHashMap<>();

    /**
     * 注册消息文件对象
     *
     * @param file 文件
     */
    @Override
    public void register(final MappedFile file) {
        final MessageIndexConstructWorker newWorker = new MessageIndexConstructWorker(file);
        final MessageIndexConstructWorker prev = workerTable.putIfAbsent(file, newWorker);
        if (prev == null) {
            newWorker.start();
        }
    }

    /**
     * 取消注册消息文件对象
     *
     * @param file 文件
     */
    @Override
    public void deregister(final MappedFile file) {
        final MessageIndexConstructWorker prev = workerTable.remove(file);
        if (prev != null) {
            prev.stop();
        }
    }

    /**
     * @description: 消息索引构建工作线程
     * @author: yuanbo
     * @date: 2022/10/7
     */
    private class MessageIndexConstructWorker extends AbstractThreadService {
        private static final String NAME = "messageIndexConstructWorker";
        private final MappedFile file;

        public MessageIndexConstructWorker(final MappedFile file) {
            super(100L);
            this.file = file;
        }

        @Override
        protected String getName() {
            return NAME;
        }

        @Override
        protected void execute() {
            constructMessageIndex(file);
        }

        /**
         * 构建消息索引
         *
         * @param file 文件
         */
        private void constructMessageIndex(final MappedFile file) {
            // TODO
        }
    }
}
