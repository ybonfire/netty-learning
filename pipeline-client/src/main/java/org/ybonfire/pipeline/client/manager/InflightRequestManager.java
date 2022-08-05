package org.ybonfire.pipeline.client.manager;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ybonfire.pipeline.client.model.RemoteRequestFuture;

/**
 * 在途请求管理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 17:40
 */
public class InflightRequestManager {
    private final Map<String/*commandId*/, RemoteRequestFuture> inflightRequestTable = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public InflightRequestManager() {
        scheduledExecutorService.scheduleAtFixedRate(this::removeExpireInflightRequests, 1000L, 1000L,
            TimeUnit.MILLISECONDS);
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
            }
        }
    }
}
