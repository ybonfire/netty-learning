package org.ybonfire.pipeline.broker.handler;

import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;

/**
 * 消息消费请求处理器
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:37
 */
public class ConsumeMessageRequestHandler {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ConsumeMessageRequestHandler INSTANCE = new ConsumeMessageRequestHandler();

    private ConsumeMessageRequestHandler() {}

    /**
     * 获取ConsumeMessageRequestHandler实例
     *
     * @return {@link ConsumeMessageRequestHandler}
     */
    public static ConsumeMessageRequestHandler getInstance() {
        return INSTANCE;
    }
}
