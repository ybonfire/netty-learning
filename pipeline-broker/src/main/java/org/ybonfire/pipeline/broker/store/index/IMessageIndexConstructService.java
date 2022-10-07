package org.ybonfire.pipeline.broker.store.index;

import org.ybonfire.pipeline.broker.store.file.MappedFile;

/**
 * 消息索引创建服务接口
 *
 * @author yuanbo
 * @date 2022-10-07 10:42
 */
public interface IMessageIndexConstructService {

    /**
     * 注册消息文件对象
     *
     * @param file 文件
     */
    void register(final MappedFile file);

    /**
     * 取消注册消息文件对象
     *
     * @param file 文件
     */
    void deregister(final MappedFile file);
}
