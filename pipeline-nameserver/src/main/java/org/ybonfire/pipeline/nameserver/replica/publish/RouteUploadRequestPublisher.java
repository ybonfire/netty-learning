package org.ybonfire.pipeline.nameserver.replica.publish;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.ybonfire.pipeline.client.config.NettyClientConfig;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;
import org.ybonfire.pipeline.nameserver.client.INameServerClient;
import org.ybonfire.pipeline.nameserver.client.impl.NameServerClientImpl;
import org.ybonfire.pipeline.nameserver.model.PeerNode;
import org.ybonfire.pipeline.nameserver.replica.peer.PeerManagerProvider;
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
    private final INameServerClient nameServerClient = new NameServerClientImpl(new NettyClientConfig());
    private final ExecutorService publishExecutors = ThreadPoolUtil.getRouteUploadRequestPublishExecutorService();

    /**
     * @description: 广播路由上报请求
     * @param:
     * @return:
     * @date: 2022/08/12 22:09:10
     */
    public void publish(final IRemotingRequest<RouteUploadRequest> request) {
        final Set<PeerNode> peers = PeerManagerProvider.getInstance().getPeers();

        final int quorum = peers.size() / 2 + 1;
        final CountDownLatch latch = new CountDownLatch(quorum);
        for (final PeerNode peer : peers) {
            final RouteUploadRequestPublishThreadTask task =
                buildRouteUploadRequestPublishThreadTask(request, peer, latch);
            publishExecutors.submit(task);
        }
        try {
            latch.await(request.getTimeoutMillis(), TimeUnit.MILLISECONDS);
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
}
