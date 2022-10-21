package org.ybonfire.pipeline.client.inflight;

import org.ybonfire.pipeline.client.exception.ReadTimeoutException;
import org.ybonfire.pipeline.client.model.RemoteRequestFuture;
import org.ybonfire.pipeline.common.lifecycle.ILifeCycle;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 在途请求管理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 17:40
 */
public class InflightRequestManager implements ILifeCycle {
    private static final InflightRequestManager INSTANCE = new InflightRequestManager();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final Map<String/*commandId*/, RemoteRequestFuture> inflightRequestTable = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private InflightRequestManager() {}

    /**
     * @description: 启动在途请求管理器
     * @param:
     * @return:
     * @date: 2022/10/12 10:22:20
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            scheduledExecutorService.scheduleAtFixedRate(this::removeExpireInflightRequests, 1000L, 1000L,
                TimeUnit.MILLISECONDS);
        }
    }

    /**
     * @description: 判断在途请求管理器是否启动
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:37
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * @description: 关闭在途请求管理器
     * @param:
     * @return:
     * @date: 2022/10/12 10:23:37
     */
    @Override
    public void shutdown() {
        if (isStarted.compareAndSet(true, false)) {
            scheduledExecutorService.shutdown();
        }
    }

    /**
     * @description: 添加在途请求
     * @param:
     * @return:
     * @date: 2022/05/23 17:41:56
     */
    public void add(final RemoteRequestFuture future) {
        inflightRequestTable.put(future.getRequest().getId(), future);
    }

    /**
     * @description: 获取在途请求
     * @param:
     * @return:
     * @date: 2022/05/23 17:42:08
     */
    public Optional<RemoteRequestFuture> get(final String id) {
        return Optional.ofNullable(inflightRequestTable.get(id));
    }

    /**
     * @description: 移除在途请求
     * @param:
     * @return:
     * @date: 2022/05/23 17:42:15
     */
    public void remove(final String id) {
        inflightRequestTable.remove(id);
    }

    /**
     * @description: 移除过期的在途请求
     * @param:
     * @return:
     * @date: 2022/06/02 10:02:47
     */
    private void removeExpireInflightRequests() {
        final Iterator<Map.Entry<String, RemoteRequestFuture>> iterator =
            this.inflightRequestTable.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, RemoteRequestFuture> entry = iterator.next();
            final RemoteRequestFuture future = entry.getValue();
            if (future.isExpired()) {
                iterator.remove();
                future.complete(new ReadTimeoutException());
            }
        }
    }

    /**
     * 获取InflightRequestManager实例
     *
     * @return {@link InflightRequestManager}
     */
    public static InflightRequestManager getInstance() {
        return INSTANCE;
    }
}
