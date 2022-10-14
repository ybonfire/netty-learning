package org.ybonfire.pipeline.broker.callback;

import org.ybonfire.pipeline.broker.model.topic.TopicConfigUpdateEvent;

/**
 * TopicConfig更新事件回调接口
 *
 * @author yuanbo
 * @date 2022-10-14 13:52
 */
public interface ITopicConfigUpdateEventCallback {

    /**
     * @description: 更新事件回调
     * @param:
     * @return:
     * @date: 2022/10/14 13:59:57
     */
    void onEvent(final TopicConfigUpdateEvent event);
}
