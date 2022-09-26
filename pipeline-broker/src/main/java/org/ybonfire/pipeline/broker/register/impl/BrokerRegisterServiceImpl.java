package org.ybonfire.pipeline.broker.register.impl;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ybonfire.pipeline.broker.client.impl.NameServerClientImpl;
import org.ybonfire.pipeline.broker.constant.BrokerConstant;
import org.ybonfire.pipeline.broker.model.TopicConfig;
import org.ybonfire.pipeline.broker.register.IBrokerRegisterService;
import org.ybonfire.pipeline.broker.topic.TopicConfigManager;
import org.ybonfire.pipeline.broker.topic.provider.TopicConfigManagerProvider;
import org.ybonfire.pipeline.broker.util.ThreadPoolUtil;
import org.ybonfire.pipeline.common.thread.task.AbstractThreadTask;

/**
 * Broker注册服务
 *
 * @author yuanbo
 * @date 2022-09-23 14:44
 */
public class BrokerRegisterServiceImpl implements IBrokerRegisterService {
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final NameServerClientImpl nameServerClient = new NameServerClientImpl();
    private final TopicConfigManager topicConfigManager = TopicConfigManagerProvider.getInstance();

    /**
     * @description: 启动Broker注册服务
     * @param:
     * @return:
     * @date: 2022/09/26 11:51:05
     */
    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            nameServerClient.start();
        }
    }

    /**
     * @description: 关闭Broker注册服务
     * @param:
     * @return:
     * @date: 2022/09/26 11:51:05
     */
    @Override
    public void shutdown() {
        if (started.compareAndSet(true, false)) {
            nameServerClient.shutdown();
        }
    }

    /**
     * 将Broker注册至NameServer
     */
    @Override
    public void registerToNameServer(final List<String> nameServerAddressList) {
        final CountDownLatch latch = new CountDownLatch(nameServerAddressList.size());

        // 向Broker上报TopicConfig信息
        final List<TopicConfig> topicConfigs = topicConfigManager.selectAllTopicConfigs();
        for (final String nameServerAddress : nameServerAddressList) {
            final BrokerRegisterThreadTask task = new BrokerRegisterThreadTask(latch, topicConfigs, nameServerAddress);
            ThreadPoolUtil.getRegisterBrokerTaskExecutorService().submit(task);
        }

        try {
            latch.await(BrokerConstant.BROKER_REGISTER_WAITING_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // ignore
        }
    }

    /**
     * @description: Broker注册线程任务
     * @author: yuanbo
     * @date: 2022/9/23
     */
    private class BrokerRegisterThreadTask extends AbstractThreadTask {
        private final CountDownLatch latch;
        private final List<TopicConfig> topicConfigs;
        private final String address;

        private BrokerRegisterThreadTask(final CountDownLatch latch, final List<TopicConfig> topicConfigs,
            final String address) {
            this.latch = latch;
            this.topicConfigs = topicConfigs;
            this.address = address;
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
                nameServerClient.uploadTopicConfig(topicConfigs, address);
            } finally {
                latch.countDown();
            }
        }
    }
}
