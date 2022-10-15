package org.ybonfire.pipeline.client.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.ybonfire.pipeline.client.exception.ConnectFailedException;
import org.ybonfire.pipeline.client.exception.ConnectTimeoutException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Netty连接管理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:09
 */
public class NettyChannelManager {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final Lock lock = new ReentrantLock();
    private final Map<String, Channel> channelTable = new ConcurrentHashMap<>();
    private final Bootstrap bootstrap;

    public NettyChannelManager(final Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * @description: 添加连接
     * @param:
     * @return:
     * @date: 2022/05/24 17:45:00
     */
    public void putChannel(final String address, final Channel channel) {
        if (channel.isActive()) {
            final Channel prev = channelTable.putIfAbsent(address, channel);
            if (prev != null && prev.isActive()) {
                this.doCloseChannel(prev);
            }
        }
    }

    /**
     * @description: 尝试获取连接
     * @param:
     * @return:
     * @date: 2022/05/19 10:12:58
     */
    public Channel getOrCreateNettyChannel(final String address, final long timeoutMillis) {
        final long startTime = System.currentTimeMillis();
        try {
            if (lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    // get
                    final Channel existedChannel = channelTable.get(address);
                    if (existedChannel != null) {
                        if (existedChannel.isActive()) {
                            return existedChannel;
                        } else {
                            channelTable.remove(address);
                        }
                    }

                    // create
                    final long remainingTimeoutMillis = timeoutMillis - (System.currentTimeMillis() - startTime);
                    final Channel newChannel = createNettyChannel(address, remainingTimeoutMillis);
                    channelTable.put(address, newChannel);

                    return newChannel;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException ex) {
            // ignore
            Thread.currentThread().interrupt();
        }

        LOGGER.error("远程连接失败");
        throw new ConnectFailedException();
    }

    /**
     * @description: 关闭所有连接
     * @param:
     * @return:
     * @date: 2022/05/24 17:41:27
     */
    public void closeAllChannel() {
        lock.lock();
        try {
            this.channelTable.values().parallelStream().forEach(this::doCloseChannel);
            this.channelTable.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @description: 关闭连接
     * @param:
     * @return:
     * @date: 2022/05/24 14:33:35
     */
    public void closeChannel(final String address) {
        lock.lock();
        try {
            final Optional<Channel> channelOptional = Optional.ofNullable(this.channelTable.get(address));
            channelOptional.ifPresent(this::doCloseChannel);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @description: 关闭连接
     * @param:
     * @return:
     * @date: 2022/05/24 17:49:55
     */
    private void doCloseChannel(final Channel channel) {
        final String address = RemotingUtil.parseChannelAddress(channel);
        channel.close()
            .addListener(future -> LOGGER.info("关闭连接. Address: [" + address + "]. result:" + future.isSuccess()));
    }

    /**
     * @description: 与指定ip:port建立连接
     * @param:
     * @return:
     * @date: 2022/05/19 10:37:08
     */
    private Channel createNettyChannel(final String address, final long timeoutMills) {
        final ChannelFuture future = this.bootstrap.connect(RemotingUtil.addressToSocketAddress(address));
        if (future.awaitUninterruptibly(timeoutMills, TimeUnit.MILLISECONDS)) {
            final Channel channel = future.channel();
            if (channel.isActive()) {
                return channel;
            } else {
                // connect failed
                LOGGER.error("远程连接失败");
                throw new ConnectFailedException();
            }
        } else {
            // connect timeout
            LOGGER.error("远程连接超时");
            throw new ConnectTimeoutException();
        }
    }
}
