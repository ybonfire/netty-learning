package org.ybonfire.pipeline.broker.callback.impl;

import org.ybonfire.pipeline.broker.callback.ITopicConfigUpdateEventCallback;
import org.ybonfire.pipeline.broker.model.topic.TopicConfigUpdateEvent;

/**
 * 默认TopicConfig更新事件回调
 *
 * @author yuanbo
 * @date 2022-10-14 16:28
 */
public class DefaultTopicConfigUpdateCallback implements ITopicConfigUpdateEventCallback {
    private static final ITopicConfigUpdateEventCallback INSTANCE = new DefaultTopicConfigUpdateCallback();

    private DefaultTopicConfigUpdateCallback() {}

    /**
     * @description: 更新事件回调
     * @param:
     * @return:
     * @date: 2022/10/14 13:59:57
     */
    @Override
    public void onEvent(final TopicConfigUpdateEvent event) {
        switch (event.getType()) {
            case ADD:
                onAddEvent(event);
                break;
            case UPDATE:
                onUpdateEvent(event);
                break;
            case DELETE:
                onDeleteEvent(event);
                break;
            default:
                break;
        }
    }

    /**
     * @description: TopicConfig新增事件处理
     * @param:
     * @return:
     * @date: 2022/10/14 16:30:30
     */
    private void onAddEvent(final TopicConfigUpdateEvent event) {
        if (event == null) {
            return;
        }

        // TODO
    }

    /**
     * @description: TopicConfig更新事件处理
     * @param:
     * @return:
     * @date: 2022/10/14 16:30:32
     */
    private void onUpdateEvent(final TopicConfigUpdateEvent event) {
        if (event == null) {
            return;
        }

        // TODO
    }

    /**
     * @description: TopicConfig删除事件处理
     * @param:
     * @return:
     * @date: 2022/10/14 16:30:34
     */
    private void onDeleteEvent(final TopicConfigUpdateEvent event) {
        if (event == null) {
            return;
        }

        // TODO
    }

    /**
     * 获取ITopicConfigUpdateEventCallback实例
     *
     * @return {@link ITopicConfigUpdateEventCallback}
     */
    public static ITopicConfigUpdateEventCallback getInstance() {
        return INSTANCE;
    }
}
