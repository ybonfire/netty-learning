package org.ybonfire.netty.client.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.ybonfire.netty.common.exception.ExceptionTypeEnum;
import org.ybonfire.netty.common.model.Pair;
import org.ybonfire.netty.common.util.ExceptionUtil;
import org.ybonfire.netty.common.util.RemotingUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
     * @description: 获取所有连接
     * @param:
     * @return:
     * @date: 2022/05/24 14:30:06
     */
    public List<Pair<String, Channel>> getAllChannels() {
        return this.channelTable.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
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
     * @description: 关闭连接
     * @param:
     * @return:
     * @date: 2022/05/24 14:33:35
     */
    public void closeChannel(final String address) {
        final Optional<Channel> channelOptional = Optional.ofNullable(this.channelTable.get(address));
        channelOptional.map(channel -> channel.close().addListener(
            future -> System.out.println("关闭连接. Address: [" + address + "]. result:" + future.isSuccess())));
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
