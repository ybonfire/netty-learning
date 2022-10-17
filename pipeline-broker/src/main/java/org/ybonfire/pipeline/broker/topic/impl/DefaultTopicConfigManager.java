package org.ybonfire.pipeline.broker.topic.impl;

import org.ybonfire.pipeline.broker.callback.ITopicConfigUpdateEventCallback;
import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.exception.TopicAlreadyCreatedException;
import org.ybonfire.pipeline.broker.exception.TopicNotFoundException;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.broker.model.topic.TopicConfigUpdateEvent;
import org.ybonfire.pipeline.broker.model.topic.TopicConfigUpdateTypeEnum;
import org.ybonfire.pipeline.broker.topic.ITopicConfigManager;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.util.FileUtil;
import org.ybonfire.pipeline.common.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 默认Topic配置管理器
 *
 * @author yuanbo
 * @date 2022-09-14 15:34
 */
public class DefaultTopicConfigManager implements ITopicConfigManager {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final String TOPIC_CONFIG_STORE_PATH = BrokerConstant.BROKER_STORE_BASE_PATH + "topic";
    private static final ITopicConfigManager INSTANCE = new DefaultTopicConfigManager();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final Map<String, TopicConfig> topicConfigTable = new HashMap<>();
    private final List<ITopicConfigUpdateEventCallback> callbacks = new CopyOnWriteArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private DefaultTopicConfigManager() {}

    /**
     * @description: 启动服务
     * @param:
     * @return:
     * @date: 2022/10/12 10:22:20
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            onStart();
        }
    }

    /**
     * @description: 判断是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:37
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭服务
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:33
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            onShutdown();
        }
    }

    @Override
    public void addTopicConfig(final TopicConfig config) {
        // 确保服务已启动
        acquireOK();

        try {
            if (lock.writeLock().tryLock(BrokerConstant.TOPIC_CONFIG_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                boolean isSuccess = false;

                try {
                    final String topic = config.getTopic();
                    if (!topicConfigTable.containsKey(topic)) {
                        topicConfigTable.put(topic, config);
                        LOGGER.info("Add topic config, now:[" + config + "]");
                        isSuccess = true;
                    } else {
                        throw new TopicAlreadyCreatedException();
                    }
                } finally {
                    // 触发Topic配置变更回调
                    if (isSuccess) {
                        final TopicConfigUpdateEvent event = TopicConfigUpdateEvent.builder()
                            .type(TopicConfigUpdateTypeEnum.ADD).topic(config.getTopic()).config(config).build();
                        fireTopicConfigUpdateEventCallbacksTriggered(event);
                    }
                    lock.writeLock().unlock();
                }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // ignore
        }
    }

    /**
     * 更新主题配置
     *
     * @param config 配置
     */
    @Override
    public void updateTopicConfig(final TopicConfig config) {
        // 确保服务已启动
        acquireOK();

        try {
            if (lock.writeLock().tryLock(BrokerConstant.TOPIC_CONFIG_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                boolean isSuccess = false;

                try {
                    final String topic = config.getTopic();
                    if (topicConfigTable.containsKey(topic)) {
                        final TopicConfig prev = topicConfigTable.put(topic, config);
                        LOGGER.info("update topic config, prev:[" + prev + "]" + "now:[" + config + "]");
                        isSuccess = true;
                    } else {
                        throw new TopicNotFoundException();
                    }
                } finally {
                    // 触发Topic配置变更回调
                    if (isSuccess) {
                        final TopicConfigUpdateEvent event = TopicConfigUpdateEvent.builder()
                            .type(TopicConfigUpdateTypeEnum.UPDATE).topic(config.getTopic()).config(config).build();
                        fireTopicConfigUpdateEventCallbacksTriggered(event);
                    }
                    lock.writeLock().unlock();
                }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // ignore
        }
    }

    @Override
    public void deleteTopicConfig(String topic) {
        // 确保服务已启动
        acquireOK();

        try {
            if (lock.writeLock().tryLock(BrokerConstant.TOPIC_CONFIG_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                boolean isSuccess = false;

                try {
                    if (topicConfigTable.containsKey(topic)) {
                        topicConfigTable.remove(topic);
                        LOGGER.info("delete topic config, topic:[" + topic + "]");
                        isSuccess = true;
                    } else {
                        throw new TopicNotFoundException();
                    }
                } finally {
                    // 触发Topic配置变更回调
                    if (isSuccess) {
                        final TopicConfigUpdateEvent event = TopicConfigUpdateEvent.builder()
                            .type(TopicConfigUpdateTypeEnum.UPDATE).topic(topic).build();
                        fireTopicConfigUpdateEventCallbacksTriggered(event);
                    }
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
    @Override
    public List<TopicConfig> selectAllTopicConfigs() {
        // 确保服务已启动
        acquireOK();

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
    @Override
    public Optional<TopicConfig> selectTopicConfig(final String topic) {
        // 确保服务已启动
        acquireOK();

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

    @Override
    public Map<String, TopicConfig> selectTopicConfig(final String... topics) {
        // 确保服务已启动
        acquireOK();

        try {
            if (lock.readLock().tryLock(BrokerConstant.TOPIC_CONFIG_LOCK_WAITING_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    return Arrays.stream(topics).collect(Collectors.toMap(topic -> topic, topicConfigTable::get));
                } finally {
                    lock.readLock().unlock();
                }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            // ignore
        }

        return Collections.emptyMap();
    }

    /**
     * 本地文件持久化主题配置
     */
    @Override
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
    @Override
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
     * @description: 注册TopicConfig更新事件回调
     * @param:
     * @return:
     * @date: 2022/10/14 14:34:19
     */
    @Override
    public void register(final ITopicConfigUpdateEventCallback callback) {
        callbacks.add(callback);
    }

    /**
     * @description: 取消注册TopicConfig更新事件回调
     * @param:
     * @return:
     * @date: 2022/10/14 14:34:19
     */
    @Override
    public void deregister(final ITopicConfigUpdateEventCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * @description: 服务启动流程
     * @param:
     * @return:
     * @date: 2022/10/14 14:35:52
     */
    private void onStart() {
        reload();
        scheduledExecutorService.scheduleAtFixedRate(this::persist, 15 * 1000L, 30 * 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * @description: 服务关闭流程
     * @param:
     * @return:
     * @date: 2022/10/14 14:37:30
     */
    private void onShutdown() {
        scheduledExecutorService.shutdown();
        persist();
    }

    /**
     * @description: 确保服务已就绪
     * @param:
     * @return:
     * @date: 2022/05/19 11:49:04
     */
    private void acquireOK() {
        if (!this.isStarted.get()) {
            throw new LifeCycleException();
        }
    }

    /**
     * @description: 触发TopicConfig更新事件回调
     * @param:
     * @return:
     * @date: 2022/10/14 14:18:29
     */
    private void fireTopicConfigUpdateEventCallbacksTriggered(final TopicConfigUpdateEvent event) {
        for (final ITopicConfigUpdateEventCallback callback : callbacks) {
            callback.onEvent(event);
        }
    }

    /**
     * 获取DefaultTopicConfigManager实例
     *
     * @return {@link DefaultTopicConfigManager}
     */
    public static ITopicConfigManager getInstance() {
        return INSTANCE;
    }
}
