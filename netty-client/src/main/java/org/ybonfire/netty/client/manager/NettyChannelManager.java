package org.ybonfire.netty.client.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.ybonfire.netty.common.exception.ExceptionTypeEnum;
import org.ybonfire.netty.common.util.ExceptionUtil;
import org.ybonfire.netty.common.util.RemotingUtil;

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
    private final Lock lock = new ReentrantLock();
    private final Map<String, Channel> channelTable = new ConcurrentHashMap<>();
    private final Bootstrap bootstrap;

    public NettyChannelManager(final Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
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
     * @description: 与指定ip:port建立连接
     * @param:
     * @return:
     * @date: 2022/05/19 10:37:08
     */
    private Channel createNettyChannel(final String address, final long timeoutMills) {
        final ChannelFuture future = this.bootstrap.connect(RemotingUtil.addressToSocketAddress(address));
        if (future.awaitUninterruptibly(timeoutMills, TimeUnit.MILLISECONDS)) {
            return future.channel();
        } else {
            // connect timeout
            throw ExceptionUtil.exception(ExceptionTypeEnum.CONNECT_TIMEOUT);
        }
    }
}
