package org.ybonfire.netty.client.manager;

import org.ybonfire.netty.client.model.RemoteRequestFuture;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在途请求管理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 17:40
 */
public class InflightRequestManager {
    private final Map<String, RemoteRequestFuture> inflightRequestTable = new ConcurrentHashMap<>();

    /**
     * @description: 添加在途请求
     * @param:
     * @return:
     * @date: 2022/05/23 17:41:56
     */
    public void add(final RemoteRequestFuture future) {
        inflightRequestTable.put(future.getRequest().getCommandId(), future);
    }

    /**
     * @description: 获取在途请求
     * @param:
     * @return:
     * @date: 2022/05/23 17:42:08
     */
    public Optional<RemoteRequestFuture> get(final String requestId) {
        return Optional.ofNullable(inflightRequestTable.get(requestId));
    }

    /**
     * @description: 移除在途请求
     * @param:
     * @return:
     * @date: 2022/05/23 17:42:15
     */
    public void remove(final String requestId) {
        inflightRequestTable.remove(requestId);
    }

}
