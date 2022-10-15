package org.ybonfire.pipeline.broker.heartbeat.impl;

import org.ybonfire.pipeline.broker.client.impl.NameServerClientImpl;
import org.ybonfire.pipeline.broker.config.BrokerConfig;
import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.heartbeat.IBrokerHeartbeatService;
import org.ybonfire.pipeline.broker.model.RoleEnum;
import org.ybonfire.pipeline.broker.model.heartbeat.HeartbeatData;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.broker.role.RoleManager;
import org.ybonfire.pipeline.broker.topic.impl.DefaultTopicConfigManager;
import org.ybonfire.pipeline.broker.util.ThreadPoolUtil;
import org.ybonfire.pipeline.common.exception.LifeCycleException;
import org.ybonfire.pipeline.common.thread.task.AbstractThreadTask;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Broker心跳服务
 *
 * @author yuanbo
 * @date 2022-09-23 14:44
 */
public class BrokerHeartbeatServiceImpl implements IBrokerHeartbeatService {
    private static final IBrokerHeartbeatService INSTANCE = new BrokerHeartbeatServiceImpl();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final NameServerClientImpl nameServerClient = new NameServerClientImpl();

    private BrokerHeartbeatServiceImpl() {}

    /**
     * @description: 启动Broker注册服务
     * @param:
     * @return:
     * @date: 2022/09/26 11:51:05
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            nameServerClient.start();
        }
    }

    /**
     * @description: 判断Broker注册服务是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:37
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭Broker注册服务
     * @param:
     * @return:
     * @date: 2022/09/26 11:51:05
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            nameServerClient.shutdown();
        }
    }

    /**
     * 将Broker注册至NameServer
     */
    @Override
    public void heartbeat(final List<String> nameServerAddressList) {
        // 确保服务已启动
        acquireOK();

        final HeartbeatData heartbeatData = buildHeartbeatData();
        final CountDownLatch latch = new CountDownLatch(nameServerAddressList.size());

        // 向Nameserver上报心跳
        for (final String nameServerAddress : nameServerAddressList) {
            final HeartbeatThreadTask task = new HeartbeatThreadTask(heartbeatData, nameServerAddress, latch);
            ThreadPoolUtil.getBrokerHeartbeatTaskExecutorService().submit(task);
        }

        try {
            latch.await(BrokerConstant.HEARTBEAT_WAITING_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // ignore
        }
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
     * @description: 构造HeartbeatData
     * @param:
     * @return:
     * @date: 2022/10/15 14:23:55
     */
    private HeartbeatData buildHeartbeatData() {
        final String brokerId = BrokerConfig.getInstance().getId();
        final RoleEnum role = RoleManager.getInstance().get();
        final String address = RemotingUtil.getLocalAddress();
        final List<TopicConfig> topicConfigs = DefaultTopicConfigManager.getInstance().selectAllTopicConfigs();

        return HeartbeatData.builder().brokerId(brokerId).role(role).address(address).topicConfigs(topicConfigs)
            .build();
    }

    /**
     * 获取BrokerRegisterServiceImpl实例
     *
     * @return {@link IBrokerHeartbeatService}
     */
    public static IBrokerHeartbeatService getInstance() {
        return INSTANCE;
    }

    /**
     * @description: 心跳上报异步任务
     * @author: yuanbo
     * @date: 2022/9/23
     */
    private class HeartbeatThreadTask extends AbstractThreadTask {
        private final HeartbeatData heartbeatData;
        private final String address;
        private final CountDownLatch latch;

        private HeartbeatThreadTask(final HeartbeatData heartbeatData, final String address,
            final CountDownLatch latch) {
            this.heartbeatData = heartbeatData;
            this.address = address;
            this.latch = latch;
        }

        /**
         * @description: 注册
         * @param:
         * @return:
         * @date: 2022/09/23 17:30:44
         */
        @Override
        protected void execute() {
            try {
                nameServerClient.heartbeat(heartbeatData, address, BrokerConstant.HEARTBEAT_TIMEOUT_MILLIS);
            } finally {
                latch.countDown();
            }
        }
    }
}
