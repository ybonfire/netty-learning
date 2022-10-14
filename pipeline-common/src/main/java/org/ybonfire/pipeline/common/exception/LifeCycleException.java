package org.ybonfire.pipeline.common.exception;

/**
 * 生命周期异常
 *
 * @author yuanbo
 * @date 2022-10-13 16:21
 */
public class LifeCycleException extends RuntimeException {

    public LifeCycleException() {}

    public LifeCycleException(final String message) {
        super(message);
    }

    public LifeCycleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LifeCycleException(final Throwable cause) {
        super(cause);
    }
}
