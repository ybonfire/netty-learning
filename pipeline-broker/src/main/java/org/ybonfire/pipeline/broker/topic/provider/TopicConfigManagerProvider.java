package org.ybonfire.pipeline.broker.topic.provider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.broker.topic.TopicConfigManager;

/**
 * TopicConfigManager Provider
 *
 * @author yuanbo
 * @date 2022-09-20 17:55
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class TopicConfigManagerProvider {
    private static final TopicConfigManager INSTANCE = new TopicConfigManager();

    public static TopicConfigManager getInstance() {
        return INSTANCE;
    }
}
