package org.ybonfire.pipeline.broker.processor;

import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;

/**
 * 消息消费请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:37
 */
public final class ConsumeMessageRequestProcessor {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ConsumeMessageRequestProcessor INSTANCE = new ConsumeMessageRequestProcessor();

    private ConsumeMessageRequestProcessor() {}

    /**
     * 获取ConsumeMessageRequestProcessor实例
     *
     * @return {@link ConsumeMessageRequestProcessor}
     */
    public static ConsumeMessageRequestProcessor getInstance() {
        return INSTANCE;
    }
}
