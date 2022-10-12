package org.ybonfire.pipeline.client.model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ybonfire.pipeline.client.exception.InvokeExecuteException;
import org.ybonfire.pipeline.client.exception.ReadTimeoutException;
import org.ybonfire.pipeline.common.callback.IRequestCallback;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.IRemotingResponse;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

import io.netty.channel.Channel;

/**
 * 远程调用数据
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 12:13
 */
public class RemoteRequestFuture {
    private final String address;
    private final Channel channel;
    private final IRemotingRequest request;
    private final IRequestCallback callback;
    private final long startTimestamp = System.currentTimeMillis();
    private final long timeoutMillis;
    private final CompletableFuture<RemotingResponse> responseFuture = new CompletableFuture<>();
    private final AtomicBoolean isCompleted = new AtomicBoolean(false);
    private volatile boolean isRequestSuccess = false;
    private volatile Throwable cause;
    private volatile RemotingRequestFutureStateEnum state = RemotingRequestFutureStateEnum.FLIGHT;

    public RemoteRequestFuture(final String address, final Channel channel, final IRemotingRequest request,
        IRequestCallback callback, final long timeoutMillis) {
        this.address = address;
        this.channel = channel;
        this.request = request;
        this.callback = callback;
        this.timeoutMillis = timeoutMillis;
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
            throw new InvokeExecuteException(e);
        } catch (TimeoutException e) {
            throw new ReadTimeoutException(e);
        }
    }

    /**
     * @description: 异步请求成功
     * @param:
     * @return:
     * @date: 2022/06/02 09:56:54
     */
    public void complete(final RemotingResponse response) {
        if (isCompleted.compareAndSet(false, true)) {
            this.responseFuture.complete(response);
            this.state = RemotingRequestFutureStateEnum.RESPOND;
            if (this.callback != null) {
                this.callback.onSuccess(response);
            }
        }
    }

    /**
     * @description: 异步请求超时
     * @param:
     * @return:
     * @date: 2022/10/12 11:09:26
     */
    public void exception(final Throwable ex) {
        if (isCompleted.compareAndSet(false, true)) {
            this.cause = ex;
            this.state = RemotingRequestFutureStateEnum.FAILED;
            if (this.callback != null) {
                this.callback.onException(this.cause);
            }
        }
    }

    public String getAddress() {
        return address;
    }

    public Channel getChannel() {
        return channel;
    }

    public IRemotingRequest getRequest() {
        return request;
    }

    public boolean isRequestSuccess() {
        return isRequestSuccess;
    }

    public void setRequestSuccess(boolean requestSuccess) {
        isRequestSuccess = requestSuccess;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(final Throwable cause) {
        this.cause = cause;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public IRequestCallback getCallback() {
        return callback;
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

    public void setState(RemotingRequestFutureStateEnum state) {
        this.state = state;
    }

    public RemotingRequestFutureStateEnum getState() {
        return state;
    }
}
