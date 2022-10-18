package org.ybonfire.pipeline.producer.metadata;

import org.apache.commons.collections4.CollectionUtils;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.producer.client.impl.NameServerClientImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 元数据服务
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 21:46
 */
public class NameServers implements ILifeCycle {
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final NameServerClientImpl nameServerClient = new NameServerClientImpl();
    private final List<String> nameServerAddressList;

    public NameServers(final List<String> nameServerAddressList) {
        this.nameServerAddressList = nameServerAddressList;
    }

    /**
     * @description: 启动Nameservers管理器
     * @param:
     * @return:
     * @date: 2022/10/18 15:03:16
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            nameServerClient.start();
        }
    }

    /**
     * @description: 判断Nameservers管理器是否启动
     * @param:
     * @return:
     * @date: 2022/10/18 15:03:41
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭Nameservers管理器
     * @param:
     * @return:
     * @date: 2022/10/18 15:03:34
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            nameServerClient.shutdown();
        }
    }

    /**
     * @description: 查询所有Topic信息
     * @param:
     * @return:
     * @date: 2022/06/29 09:55:22
     */
    public List<TopicInfo> selectAllTopicInfo(final long timeoutMillis) {
        if (CollectionUtils.isEmpty(nameServerAddressList)) {
            throw new IllegalArgumentException();
        }

        // 确保服务已启动
        acquireOK();

        for (int i = 0; i < nameServerAddressList.size(); ++i) {
            final String address = nameServerAddressList.get(i);
            try {
                return nameServerClient.selectAllTopicInfo(address, timeoutMillis);
            } catch (Exception ex) {
                // ignore
            }
        }

        return Collections.emptyList();
    }

    /**
     * @description: 根据topic名称查询Topic信息
     * @param:
     * @return:
     * @date: 2022/06/27 21:36:35
     */
    public Optional<TopicInfo> selectTopicInfo(final String topic, final long timeoutMillis) {
        if (CollectionUtils.isEmpty(nameServerAddressList)) {
            throw new IllegalArgumentException();
        }

        // 确保服务已启动
        acquireOK();

        for (int i = 0; i < nameServerAddressList.size(); ++i) {
            final String address = nameServerAddressList.get(i);
            try {
                return nameServerClient.selectTopicInfo(topic, address, timeoutMillis);
            } catch (Exception ex) {
                // ignore
            }
        }

        return Optional.empty();
    }

    /**
     * @description: 确保服务已启动
     * @param:
     * @return:
     * @date: 2022/10/18 15:04:25
     */
    private void acquireOK() {
        if (!this.isStarted.get()) {
            throw new LifeCycleException();
        }
    }
}
