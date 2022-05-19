package org.ybonfire.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.ybonfire.netty.client.callback.IRequestCallback;
import org.ybonfire.netty.client.config.NettyClientConfig;
import org.ybonfire.netty.client.manager.NettyChannelManager;
import org.ybonfire.netty.client.model.RemoteRequestFuture;
import org.ybonfire.netty.common.client.IRemotingClient;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.model.RequestTypeEnum;
import org.ybonfire.netty.common.protocol.ResponseCodeConstant;
import org.ybonfire.netty.common.util.AssertUtils;
import org.ybonfire.netty.common.util.CodecUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty远程调用客户端
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 15:28
 */
public class NettyRemotingClient implements IRemotingClient {
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final Bootstrap bootstrap = new Bootstrap();
    private final NettyClientConfig config;
    private final EventLoopGroup clientEventLoopGroup;
    private final NettyChannelManager channelManager = new NettyChannelManager(bootstrap);
    private final Map<String, RemoteRequestFuture> inFlightRequests = new ConcurrentHashMap<>();

    public NettyRemotingClient(final NettyClientConfig config) {
        this.config = config;

        // eventLoopGroup
        final int eventLoopGroupThreadNums = this.config.getClientWorkerThreads();
        this.clientEventLoopGroup = buildEventLoop(eventLoopGroupThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("ClientEventLoopGroup_%d", this.threadIndex.incrementAndGet()));
            }
        });
    }

    /**
     * @description:
     * @param:
     * @return:
     * @date: 2022/05/18 15:32:53
     */
    @Override
    public void start() {

    }

    /**
     * @description:
     * @param:
     * @return:
     * @date: 2022/05/18 15:32:56
     */
    @Override
    public void shutdown() {

    }

    /**
     * @description: 构造EventLoopGroup
     * @param:
     * @return:
     * @date: 2022/05/18 12:21:07
     */
    private EventLoopGroup buildEventLoop(final int theadNums, final ThreadFactory threadFactory) {
        return new NioEventLoopGroup(theadNums, threadFactory);
    }

    /**
     * @description: 同步调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:08
     */
    @Override
    public RemotingCommand request(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException {
        acquireOK();
        return doRequest(address, request, timeoutMillis, RequestTypeEnum.SYNC);
    }

    /**
     * @description: 异步调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:13
     */
    @Override
    public void requestAsync(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException {
        acquireOK();
        doRequest(address, request, timeoutMillis, RequestTypeEnum.ASYNC);
    }

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/19 10:07:18
     */
    @Override
    public void requestOneWay(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException {
        acquireOK();
        doRequest(address, request, timeoutMillis, RequestTypeEnum.ONEWAY);
    }

    /**
     * @description: 构造RemoteRequestFuture
     * @param:
     * @return:
     * @date: 2022/05/19 13:26:46
     */
    private RemoteRequestFuture buildRemoteRequestFuture(final String address, final Channel channel,
        final RemotingCommand request) {
        return new RemoteRequestFuture(address, channel, request);
    }

    /**
     * @description: 判断客户端是否启动
     * @param:
     * @return:
     * @date: 2022/05/19 11:49:04
     */
    private void acquireOK() {
        if (!this.started.get()) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @description: 执行调用请求
     * @param:
     * @return:
     * @date: 2022/05/19 14:10:22
     */
    private RemotingCommand doRequest(final String address, final RemotingCommand request, final long timeoutMillis,
        final RequestTypeEnum type) throws InterruptedException {
        final long startTimestamp = System.currentTimeMillis();

        AssertUtils.notNull(address);
        AssertUtils.notNull(request);
        AssertUtils.notNull(type);

        /** 建立连接 **/
        final Channel channel = this.channelManager.getOrCreateNettyChannel(address, timeoutMillis);
        final RemoteRequestFuture future = buildRemoteRequestFuture(address, channel, request);

        /** 缓存在途请求 **/
        this.inFlightRequests.put(request.getRequestId(), future);

        /** 发送请求 **/
        final long remainingTimeoutMillis = timeoutMillis - System.currentTimeMillis() - startTimestamp;
        switch (type) {
            case SYNC:
                final RemotingCommand response = doRequestSync(future, remainingTimeoutMillis);
                return response;
            case ASYNC:
                doRequestAsync(future, remainingTimeoutMillis, null);
                return null;
            case ONEWAY:
                doRequestOneWay(future, remainingTimeoutMillis);
                return null;
            default:
                // TODO
                throw new UnsupportedOperationException();
        }
    }

    /**
     * @description: 执行同步请求
     * @param:
     * @return:
     * @date: 2022/05/19 15:03:49
     */
    private RemotingCommand doRequestSync(final RemoteRequestFuture future, final long timeoutMillis)
        throws InterruptedException {
        /** 发送请求 **/
        future.getChannel().writeAndFlush(future.getRequest()).addListener(f -> {
            if (f.isSuccess()) {
                future.setRequestSuccess(true);
            } else {
                future.setRequestSuccess(false);
                future.setCause(f.cause());

                // 请求发送失败，移除在途的请求
                this.inFlightRequests.remove(future.getRequest().getRequestId());
            }
        });

        /** 等待响应 **/
        final RemotingCommand response = future.get(timeoutMillis);
        if (response == null) {
            if (future.isRequestSuccess()) { // 请求成功，但未响应
                return RemotingCommand.createResponseCommand(ResponseCodeConstant.SERVER_NOT_RESPONSE,
                    CodecUtil.toBytes("服务未响应"), future.getRequest().getRequestId());
            } else { // 请求失败
                return RemotingCommand.createResponseCommand(ResponseCodeConstant.REQUEST_TIMEOUT,
                    CodecUtil.toBytes("请求超时"), future.getRequest().getRequestId());
            }
        }

        return response;
    }

    /**
     * @description: 执行异步调用
     * @param:
     * @return:
     * @date: 2022/05/19 15:05:58
     */
    private void doRequestAsync(final RemoteRequestFuture future, final long timeoutMillis,
        final IRequestCallback callback) {

    }

    /**
     * @description: 执行单向调用
     * @param:
     * @return:
     * @date: 2022/05/19 15:06:45
     */
    private void doRequestOneWay(final RemoteRequestFuture future, final long timeoutMillis) {

    }
}
