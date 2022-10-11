package org.ybonfire.pipeline.broker.topic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.model.TopicConfig;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.util.FileUtil;
import org.ybonfire.pipeline.common.util.JsonUtil;

/**
 * Topic管理器
 *
 * @author yuanbo
 * @date 2022-09-14 15:34
 */
public class TopicConfigManager {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final String TOPIC_CONFIG_STORE_PATH = BrokerConstant.BROKER_STORE_BASE_PATH + "topic";
    private static final TopicConfigManager INSTANCE = new TopicConfigManager();
    private final Map<String, TopicConfig> topicConfigTable = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private TopicConfigManager() {
        reload();
        scheduledExecutorService.scheduleAtFixedRate(this::persist, 15 * 1000L, 30 * 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * 更新主题配置
     *
     * @param config 配置
     */
    public void updateTopicConfig(final TopicConfig config) {
        try {
            if (lock.writeLock().tryLock(BrokerConstant.TOPIC_CONFIG_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    final String topic = config.getTopic();
                    final TopicConfig prev = topicConfigTable.put(topic, config);
                    if (prev != null) {
                        LOGGER.info("update topic config, prev:[" + prev + "]" + "now:[" + config + "]");
                    } else {
                        LOGGER.info("create new topic config. [" + config + "]");
                    }

                    // 持久化
                    persist();
                } finally {
                    lock.writeLock().unlock();
                }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // ignore
        }
    }

    /**
     * 查询所有主题配置
     *
     * @return {@link List}<{@link TopicConfig}>
     */
    public List<TopicConfig> selectAllTopicConfigs() {
        try {
            if (lock.readLock().tryLock(BrokerConstant.TOPIC_CONFIG_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    return new ArrayList<>(topicConfigTable.values());
                } finally {
                    lock.readLock().unlock();
                }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // ignore
        }

        return Collections.emptyList();
    }

    /**
     * 查询主题配置
     *
     * @param topic 主题
     * @return {@link Optional}
     */
    public Optional<TopicConfig> selectTopicConfig(final String topic) {
        try {
            if (lock.readLock().tryLock(BrokerConstant.TOPIC_CONFIG_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    return Optional.ofNullable(topicConfigTable.get(topic));
                } finally {
                    lock.readLock().unlock();
                }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // ignore
        }

        return Optional.empty();
    }

    /**
     * 本地文件持久化主题配置
     */
    public synchronized void persist() {
        try {
            final String content = JsonUtil.encode(this.topicConfigTable);
            if (content != null) {
                FileUtil.writeToFile(TOPIC_CONFIG_STORE_PATH, content);
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * 加载本地文件持久化的主题配置
     */
    public synchronized void reload() {
        final Map<String, TopicConfig> configs = new HashMap<>();

        try {
            final String content = FileUtil.readFromFile(TOPIC_CONFIG_STORE_PATH);
            if (content != null) {
                final Map<String, Object> jsonTable = JsonUtil.decode(content, Map.class);
                for (final Map.Entry<String, Object> entry : jsonTable.entrySet()) {
                    final String topic = entry.getKey();
                    final String topicInfoJson = JsonUtil.encode(entry.getValue());
                    final TopicConfig topicConfig = JsonUtil.decode(topicInfoJson, TopicConfig.class);
                    configs.put(topic, topicConfig);
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            this.topicConfigTable.clear();
            this.topicConfigTable.putAll(configs);
        }
    }

    /**
     * 获取TopicConfigManager实例
     *
     * @return {@link TopicConfigManager}
     */
    public static TopicConfigManager getInstance() {
        return INSTANCE;
    }
}
