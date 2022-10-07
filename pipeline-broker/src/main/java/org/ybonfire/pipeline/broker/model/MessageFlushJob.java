package org.ybonfire.pipeline.broker.model;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.broker.store.file.MappedFile;

import java.util.concurrent.CompletableFuture;

/**
 * 消息刷盘任务
 *
 * @author yuanbo
 * @date 2022-10-06 17:00
 */
@Builder
@Data
public class MessageFlushJob {
    private final MappedFile file;
    private final int flushOffset;
    private final int attemptTimes;
    private final CompletableFuture<MessageFlushResultEnum> future = new CompletableFuture<>();

    public CompletableFuture<MessageFlushResultEnum> getFuture() {
        return future;
    }

    public void complete(final MessageFlushResultEnum result) {
        future.complete(result);
    }
}
