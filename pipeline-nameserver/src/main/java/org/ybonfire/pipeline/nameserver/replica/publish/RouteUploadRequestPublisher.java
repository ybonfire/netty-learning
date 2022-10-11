package org.ybonfire.pipeline.nameserver.replica.publish;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;
import org.ybonfire.pipeline.nameserver.client.impl.NameServerClientImpl;
import org.ybonfire.pipeline.nameserver.constant.NameServerConstant;
import org.ybonfire.pipeline.nameserver.model.PeerNode;
import org.ybonfire.pipeline.nameserver.replica.peer.PeerManager;
import org.ybonfire.pipeline.nameserver.thread.RouteUploadRequestPublishThreadTask;
import org.ybonfire.pipeline.nameserver.thread.RouteUploadRequestPublishThreadTaskBuilder;
import org.ybonfire.pipeline.nameserver.util.ThreadPoolUtil;

/**
 * 路由数据广播器
 *
 * @author Bo.Yuan5
 * @date 2022-08-12 22:05
 */
public class RouteUploadRequestPublisher {
    private static final RouteUploadRequestPublisher INSTANCE = new RouteUploadRequestPublisher();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final NameServerClientImpl nameServerClient = new NameServerClientImpl(new NettyClientConfig());
    private final ExecutorService publishExecutors = ThreadPoolUtil.getRouteUploadRequestPublishExecutorService();

    private RouteUploadRequestPublisher() {}

    /**
     * @description: 启动路由数据广播其
     * @param:
     * @return:
     * @date: 2022/09/26 16:15:12
     */
    public void start() {
        if (started.compareAndSet(false, true)) {
            nameServerClient.start();
        }
    }

    /**
     * @description: 关闭路由数据广播器
     * @param:
     * @return:
     * @date: 2022/09/26 16:15:21
     */
    public void shutdown() {
        if (started.compareAndSet(true, false)) {
            nameServerClient.shutdown();
        }
    }

    /**
     * @description: 广播路由上报请求
     * @param:
     * @return:
     * @date: 2022/08/12 22:09:10
     */
    public void publish(final IRemotingRequest<RouteUploadRequest> request) {
        final Set<PeerNode> peers = PeerManager.getInstance().getPeers();

        final int quorum = peers.size() / 2 + 1;
        final CountDownLatch latch = new CountDownLatch(quorum);
        for (final PeerNode peer : peers) {
            final RouteUploadRequestPublishThreadTask task =
                buildRouteUploadRequestPublishThreadTask(request, peer, latch);
            publishExecutors.submit(task);
        }
        try {
            latch.await(NameServerConstant.ROUTE_UPLOAD_PUBLISH_TIME_OUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // ignored
        }
    }

    /**
     * @description: 构造RouteUploadRequestPublishThreadTask
     * @param:
     * @return:
     * @date: 2022/08/12 22:32:47
     */
    private RouteUploadRequestPublishThreadTask buildRouteUploadRequestPublishThreadTask(
        final IRemotingRequest<RouteUploadRequest> request, final PeerNode peer, final CountDownLatch latch) {
        return RouteUploadRequestPublishThreadTaskBuilder.build(request, peer, latch, nameServerClient);
    }

    /**
     * 获取RouteUploadRequestPublisher实例
     *
     * @return {@link RouteUploadRequestPublisher}
     */
    public static RouteUploadRequestPublisher getInstance() {
        return INSTANCE;
    }
}
