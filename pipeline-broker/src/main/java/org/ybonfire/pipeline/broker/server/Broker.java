package org.ybonfire.pipeline.broker.server;

import org.ybonfire.pipeline.broker.config.BrokerConfig;
import org.ybonfire.pipeline.broker.model.RoleEnum;
import org.ybonfire.pipeline.broker.processor.CreateTopicRequestProcessor;
import org.ybonfire.pipeline.broker.processor.DeleteTopicRequestProcessor;
import org.ybonfire.pipeline.broker.processor.ProduceMessageRequestProcessor;
import org.ybonfire.pipeline.broker.processor.UpdateTopicRequestProcessor;
import org.ybonfire.pipeline.broker.heartbeat.impl.BrokerHeartbeatServiceImpl;
import org.ybonfire.pipeline.broker.role.RoleManager;
import org.ybonfire.pipeline.broker.store.message.impl.DefaultMessageStoreServiceImpl;
import org.ybonfire.pipeline.broker.topic.impl.DefaultTopicConfigManager;
import org.ybonfire.pipeline.broker.util.ThreadPoolUtil;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.server.NettyRemotingServer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Broker服务
 *
 * @author Bo.Yuan5
 * @date 2022-08-24 21:36
 */
public final class Broker extends NettyRemotingServer {
    private final AtomicBoolean isStarted = new AtomicBoolean();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final List<String> nameServerAddressList;

    public Broker(final BrokerConfig config, final List<String> nameServerAddressList) {
        super(config);
        RoleManager.getInstance().set(RoleEnum.of(config.getRole()));
        this.nameServerAddressList = nameServerAddressList;
    }

    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            onstart();
        }
    }

    /**
     * @description: 判断Broker是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:37
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            onShutdown();
        }
    }

    @Override
    protected void registerRequestProcessor() {
        // ProduceMessageRequestProcessor
        registerProduceMessageRequestProcessor();
        // ConsumeMessageRequestProcessor
        registerConsumeMessageRequestProcessor();
        // UpdateTopicRequestProcessor
        registerUpdateTopicRequestProcessor();
        // CreateTopicRequestProcessor
        registerCreateTopicRequestProcessor();
        // DeleteTopicRequestProcessor
        registerDeleteTopicRequestProcessor();
    }

    /**
     * 注册Broker至NameServer
     */
    private void registerToNameServer() {
        BrokerHeartbeatServiceImpl.getInstance().heartbeat(this.nameServerAddressList);
    }

    /**
     * 注册ProduceMessageRequestProcessor
     */
    private void registerProduceMessageRequestProcessor() {
        final ExecutorService produceMessageRequestProcessorExecutor =
            ThreadPoolUtil.getProduceMessageProcessorExecutorService();
        registerRequestProcessor(RequestEnum.PRODUCE_MESSAGE.getCode(), ProduceMessageRequestProcessor.getInstance(),
            produceMessageRequestProcessorExecutor);
    }

    /**
     * 注册CreateTopicRequestProcessor
     */
    private void registerCreateTopicRequestProcessor() {
        final ExecutorService brokerAdminExecutorService = ThreadPoolUtil.getBrokerAdminExecutorService();
        registerRequestProcessor(RequestEnum.CREATE_TOPIC.getCode(), CreateTopicRequestProcessor.getInstance(),
            brokerAdminExecutorService);
    }

    /**
     * 注册UpdateTopicRequestProcessor
     */
    private void registerUpdateTopicRequestProcessor() {
        final ExecutorService brokerAdminExecutorService = ThreadPoolUtil.getBrokerAdminExecutorService();
        registerRequestProcessor(RequestEnum.UPDATE_TOPIC.getCode(), UpdateTopicRequestProcessor.getInstance(),
            brokerAdminExecutorService);
    }

    /**
     * 注册DeleteTopicRequestProcessor
     */
    private void registerDeleteTopicRequestProcessor() {
        final ExecutorService brokerAdminExecutorService = ThreadPoolUtil.getBrokerAdminExecutorService();
        registerRequestProcessor(RequestEnum.DELETE_TOPIC.getCode(), DeleteTopicRequestProcessor.getInstance(),
            brokerAdminExecutorService);
    }

    /**
     * 注册ConsumeMessage请求处理器
     */
    private void registerConsumeMessageRequestProcessor() {
        final ExecutorService consumeMessageRequestProcessorExecutor =
            ThreadPoolUtil.getConsumeMessageProcessorExecutorService();
    }

    /**
     * @description: 启动Broker
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:48
     */
    private void onstart() {
        super.start();

        // 启动Topic配置管理服务
        DefaultTopicConfigManager.getInstance().start();

        // 启动消息存储服务
        DefaultMessageStoreServiceImpl.getInstance().start();

        // 启动Broker注册服务
        BrokerHeartbeatServiceImpl.getInstance().start();

        // 启动注册定时任务,定时向NameServer上报信息
        if (RoleManager.getInstance().get() == RoleEnum.LEADER) {
            scheduledExecutorService.scheduleAtFixedRate(this::registerToNameServer, 5 * 1000L, 10 * 1000L,
                TimeUnit.MILLISECONDS);
        }
    }

    /**
     * @description: 关闭Broker
     * @param:
     * @return:
     * @date: 2022/10/11 16:36:48
     */
    private void onShutdown() {
        super.shutdown();

        // 关闭注册定时任务
        if (RoleManager.getInstance().get() == RoleEnum.LEADER) {
            scheduledExecutorService.shutdown();
        }

        // 关闭Broker注册服务
        BrokerHeartbeatServiceImpl.getInstance().shutdown();

        // 关闭消息存储服务
        DefaultMessageStoreServiceImpl.getInstance().shutdown();

        // 关闭Topic配置管理服务
        DefaultTopicConfigManager.getInstance().shutdown();
    }
}
