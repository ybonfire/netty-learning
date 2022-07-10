package org.ybonfire.pipeline.client.manager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Netty连接管理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:09
 */
public class NettyChannelManager {
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
        try {
            lock.lock();

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
            final Channel newChannel = createNettyChannel(address, timeoutMillis);
            channelTable.put(address, newChannel);

            return newChannel;
        } finally {
            lock.unlock();
        }
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
        channel.close().addListener(
            future -> System.out.println("关闭连接. Address: [" + address + "]. result:" + future.isSuccess()));
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
                throw ExceptionUtil.exception(ExceptionTypeEnum.CONNECT_FAILED);
            }
        } else {
            // connect timeout
            throw ExceptionUtil.exception(ExceptionTypeEnum.CONNECT_TIMEOUT);
        }
    }
}
