package org.ybonfire.pipeline.client.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestResponse;
import org.ybonfire.pipeline.common.util.AssertUtils;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程连接抽象
 *
 * @author yuanbo
 * @date 2022-10-17 11:01
 */
public class Connection {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final String id;
    private final String address;
    private final Channel channel;
    private final Map<String, Object> metadata = new ConcurrentHashMap<>();

    Connection(final String id, final String address, final Channel channel) {
        AssertUtils.notNull(id);
        AssertUtils.notNull(address);
        AssertUtils.notNull(channel);

        this.id = id;
        this.address = address;
        this.channel = channel;
    }

    /**
     * @description: 判断连接状态是否正常
     * @param:
     * @return:
     * @date: 2022/10/17 11:18:09
     */
    public boolean isOK() {
        return this.channel != null && this.channel.isActive() && this.channel.isWritable();
    }

    /**
     * @description: 添加Metadata
     * @param:
     * @return:
     * @date: 2022/10/17 16:33:15
     */
    public void addMetadata(final String key, final Object value) {

    }

    /**
     * @description: 向连接发送消息(请求、响应)
     * @param:
     * @return:
     * @date: 2022/10/17 14:49:10
     */
    public ChannelFuture write(final IRemotingRequestResponse requestResponse) {
        if (requestResponse == null) {

        }

        return this.channel.writeAndFlush(requestResponse);
    }

    /**
     * @description: 获取连接目标地址
     * @param:
     * @return:
     * @date: 2022/10/17 11:20:58
     */
    public String getRemoteAddress() {
        return RemotingUtil.parseChannelAddress(channel);
    }

    /**
     * @description: 关闭连接
     * @param:
     * @return:
     * @date: 2022/10/17 11:18:03
     */
    public void close() {
        if (channel != null) {
            RemotingUtil.closeChannel(channel);
        }
    }

    /**
     * @description: 包装Netty Channel
     * @param:
     * @return:
     * @date: 2022/10/17 15:30:35
     */
    public static Connection wrap(final Channel channel) {
        if (channel == null) {
            return null;
        }

        final String id = channel.id().asShortText();
        final String address = RemotingUtil.parseChannelAddress(channel);
        return new Connection(id, address, channel);
    }
}
