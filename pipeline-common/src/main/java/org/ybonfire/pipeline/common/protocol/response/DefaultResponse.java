package org.ybonfire.pipeline.common.protocol.response;

import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;

/**
 * 默认
 *
 * @author Bo.Yuan5
 * @date 2022-07-13 10:26
 */
public final class DefaultResponse implements IRemotingResponseBody {
    private final String message;

    private DefaultResponse(final String message) {
        this.message = message;
    }

    public static DefaultResponse create(final String message) {
        return new DefaultResponse(message);
    }
}
