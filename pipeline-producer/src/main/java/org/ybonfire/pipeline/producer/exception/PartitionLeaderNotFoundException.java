package org.ybonfire.pipeline.producer.exception;

import org.ybonfire.pipeline.client.exception.ClientException;

/**
 * 未知分区Leader异常
 *
 * @author yuanbo
 * @date 2022/09/09 18:10:48
 */
public class PartitionLeaderNotFoundException extends ClientException {

    public PartitionLeaderNotFoundException() {}

    public PartitionLeaderNotFoundException(final String message) {
        super(message);
    }

    public PartitionLeaderNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PartitionLeaderNotFoundException(final Throwable cause) {
        super(cause);
    }
}
