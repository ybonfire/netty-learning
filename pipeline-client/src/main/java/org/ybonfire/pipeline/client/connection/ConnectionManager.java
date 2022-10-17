package org.ybonfire.pipeline.client.connection;

import org.ybonfire.pipeline.client.exception.ConnectFailedException;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 连接管理器
 *
 * @author yuanbo
 * @date 2022-10-17 11:01
 */
public final class ConnectionManager {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ConnectionManager INSTANCE = new ConnectionManager();
    private final Lock lock = new ReentrantLock();
    private final Map<String/*remote address*/, Connection> connectionTable = new ConcurrentHashMap<>();

    private ConnectionManager() {}

    /**
     * @description: 添加连接
     * @param:
     * @return:
     * @date: 2022/10/17 13:38:20
     */
    public void add(final Connection connection) {
        if (connection != null && connection.isOK()) {
            final String address = connection.getRemoteAddress();
            final Connection prev = connectionTable.putIfAbsent(address, connection);
            if (prev != null && prev.isOK()) {
                prev.close();
            }
        }
    }

    /**
     * @description: 获取指定地址的连接
     * @param:
     * @return:
     * @date: 2022/10/17 13:45:07
     */
    public Connection get(final String address, final long timeoutMillis) {
        if (address == null) {
            return null;
        }

        final long startTime = System.currentTimeMillis();
        try {
            if (lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    // get
                    final Connection existedConnection = connectionTable.get(address);
                    if (existedConnection != null) {
                        if (existedConnection.isOK()) {
                            return existedConnection;
                        } else {
                            existedConnection.close();
                            connectionTable.remove(address);
                        }
                    }

                    // create
                    final long remainingTimeoutMillis = timeoutMillis - (System.currentTimeMillis() - startTime);
                    final Connection newConnection = create(address, remainingTimeoutMillis);
                    connectionTable.put(address, newConnection);

                    return newConnection;
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
     * @description: 移除指定地址的连接
     * @param:
     * @return:
     * @date: 2022/10/17 15:44:23
     */
    public void remove(final String address) {
        if (address == null) {
            return;
        }

        lock.lock();
        try {
            final Connection connection = connectionTable.remove(address);
            if (connection != null) {
                connection.close();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * @description: 移除全部连接
     * @param:
     * @return:
     * @date: 2022/10/17 15:44:23
     */
    public void removeAll() {
        lock.lock();
        try {
            this.connectionTable.values().parallelStream().forEach(Connection::close);
            this.connectionTable.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @description: 与指定地址建立连接
     * @param:
     * @return:
     * @date: 2022/05/19 10:37:08
     */
    private Connection create(final String address, final long timeoutMillis) {
        return ConnectionFactory.getInstance().create(address, timeoutMillis);
    }

    /**
     * 获取ConnectionManager实例
     *
     * @return {@link ConnectionManager}
     */
    public static ConnectionManager getInstance() {
        return INSTANCE;
    }
}
