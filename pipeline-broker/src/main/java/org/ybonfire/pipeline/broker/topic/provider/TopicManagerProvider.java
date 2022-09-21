package org.ybonfire.pipeline.broker.topic.provider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.broker.topic.TopicManager;

/**
 * TopicManager Provider
 *
 * @author yuanbo
 * @date 2022-09-20 17:55
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class TopicManagerProvider {
    private static final TopicManager INSTANCE = new TopicManager();

    public static TopicManager getInstance() {
        return INSTANCE;
    }
}
