package org.ybonfire.pipeline.broker.model.store;

import java.util.concurrent.CompletableFuture;

import org.ybonfire.pipeline.broker.store.message.MessageLog;

import lombok.Builder;
import lombok.Data;

/**
 * 消息刷盘任务
 *
 * @author yuanbo
 * @date 2022-10-06 17:00
 */
@Builder
@Data
public final class MessageLogFlushJob {
    private final MessageLog messageLog;
    private final CompletableFuture<MessageFlushResultEnum> future = new CompletableFuture<>();

    public CompletableFuture<MessageFlushResultEnum> getFuture() {
        return future;
    }

    public void complete(final MessageFlushResultEnum result) {
        future.complete(result);
    }
}
