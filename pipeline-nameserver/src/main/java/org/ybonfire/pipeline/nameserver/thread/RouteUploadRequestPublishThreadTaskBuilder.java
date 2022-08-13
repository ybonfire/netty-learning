package org.ybonfire.pipeline.nameserver.thread;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.request.RouteUploadRequest;
import org.ybonfire.pipeline.nameserver.client.INameServerClient;
import org.ybonfire.pipeline.nameserver.model.PeerNode;

import java.util.concurrent.CountDownLatch;

/**
 * RouteUploadRequestPublish异步任务构造器
 *
 * @author Bo.Yuan5
 * @date 2022-08-12 22:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RouteUploadRequestPublishThreadTaskBuilder {

    /**
     * @description: 构造RouteUploadRequestPublish异步任务
     * @param:
     * @return:
     * @date: 2022/08/12 22:31:51
     */
    public static RouteUploadRequestPublishThreadTask build(final IRemotingRequest<RouteUploadRequest> request,
        final PeerNode peer, final CountDownLatch latch, final INameServerClient nameServerClient) {
        return new RouteUploadRequestPublishThreadTask(request, peer, latch, nameServerClient);
    }
}
