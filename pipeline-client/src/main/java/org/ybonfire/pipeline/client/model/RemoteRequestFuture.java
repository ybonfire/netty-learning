package org.ybonfire.pipeline.client.model;

import org.ybonfire.pipeline.client.connection.Connection;
import org.ybonfire.pipeline.client.exception.RemotingInvokeExecuteException;
import org.ybonfire.pipeline.client.exception.ReadTimeoutException;
import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 远程调用数据
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 12:13
 */
public class RemoteRequestFuture {
    private final IRemotingRequest request;
    private final IRequestCallback callback;
    private final long timeoutMillis;
    private final Connection connection;
    private final long startTimestamp = System.currentTimeMillis();
    private final CompletableFuture<RemotingResponse> responseFuture = new CompletableFuture<>();
    private volatile RemotingInvokeExecuteException cause;
    private AtomicReference<RemotingRequestFutureStateEnum> state =
        new AtomicReference<>(RemotingRequestFutureStateEnum.LAUNCH);

    public RemoteRequestFuture(final IRemotingRequest request, IRequestCallback callback, final long timeoutMillis,
        final Connection connection) {
        this.request = request;
        this.callback = callback;
        this.timeoutMillis = timeoutMillis;
        this.connection = connection;
    }

    /**
     * @description: 等待远程调用请求执行结果
     * @param:
     * @return:
     * @date: 2022/06/02 09:56:41
     */
    public IRemotingResponse get(final long timeoutMillis) throws InterruptedException {
        try {
            return this.responseFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw new RemotingInvokeExecuteException(e);
        } catch (TimeoutException e) {
            throw new ReadTimeoutException(e);
        }
    }

    /**
     * @description: 请求发送
     * @param:
     * @return:
     * @date: 2022/10/20 17:49:15
     */
    public void launch() {
        state.compareAndSet(RemotingRequestFutureStateEnum.LAUNCH, RemotingRequestFutureStateEnum.INFLIGHT);
    }

    /**
     * @description: 请求成功
     * @param:
     * @return:
     * @date: 2022/06/02 09:56:54
     */
    public void complete(final RemotingResponse response) {
        if (state.compareAndSet(RemotingRequestFutureStateEnum.INFLIGHT, RemotingRequestFutureStateEnum.RESPOND)) {
            this.responseFuture.complete(response);
            if (this.callback != null) {
                this.callback.onSuccess(response);
            }
        }
    }

    /**
     * @description: 请求异常
     * @param:
     * @return:
     * @date: 2022/10/12 11:09:26
     */
    public void complete(final Throwable ex) {
        if (state.compareAndSet(RemotingRequestFutureStateEnum.LAUNCH, RemotingRequestFutureStateEnum.FAILED)) {
            this.cause = new RemotingInvokeExecuteException(ex);
            if (this.callback != null) {
                this.callback.onException(this.cause);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public IRemotingRequest getRequest() {
        return request;
    }

    public RemotingInvokeExecuteException getCause() {
        return cause;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public IRequestCallback getCallback() {
        return callback;
    }

    /**
     * @description: 判断请求是否发送成功
     * @param:
     * @return:
     * @date: 2022/10/20 18:28:29
     */
    public boolean isInflight() {
        return this.state.get() == RemotingRequestFutureStateEnum.INFLIGHT;
    }

    /**
     * @description: 判断请求是否过期
     * @param:
     * @return:
     * @date: 2022/10/12 10:56:24
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > startTimestamp + timeoutMillis;
    }
}
