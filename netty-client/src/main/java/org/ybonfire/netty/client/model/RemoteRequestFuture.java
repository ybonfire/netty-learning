package org.ybonfire.netty.client.model;

import io.netty.channel.Channel;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.exception.ExceptionTypeEnum;
import org.ybonfire.netty.common.util.ExceptionUtil;

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
    private final String address;
    private final Channel channel;
    private final RemotingCommand request;
    private final CompletableFuture<RemotingCommand> responseFuture = new CompletableFuture<>();
    private volatile boolean isRequestSuccess = false;
    private volatile Throwable cause;

    public RemoteRequestFuture(final String address, final Channel channel, final RemotingCommand request) {
        this.address = address;
        this.channel = channel;
        this.request = request;
    }

    public RemotingCommand get(final long timeoutMillis) throws InterruptedException {
        try {
            return this.responseFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.UNKNOWN, e);
        } catch (TimeoutException e) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.CONNECT_TIMEOUT, e);
        }
    }

    public void complete(final RemotingCommand response) {
        responseFuture.complete(response);
    }

    public String getAddress() {
        return address;
    }

    public Channel getChannel() {
        return channel;
    }

    public RemotingCommand getRequest() {
        return request;
    }

    public CompletableFuture<RemotingCommand> getResponseFuture() {
        return responseFuture;
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
}
