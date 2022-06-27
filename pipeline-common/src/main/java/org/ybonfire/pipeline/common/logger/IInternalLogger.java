package org.ybonfire.pipeline.common.logger;

/**
 * 内部日志接口
 *
 * @author Bo.Yuan5
 * @date 2022-06-01 15:22
 */
public interface IInternalLogger {
    void trace(final String message);

    void trace(final String message, final Throwable ex);

    void debug(final String message);

    void debug(final String message, final Throwable ex);

    void info(final String message);

    void info(final String message, final Throwable ex);

    void warn(final String message);

    void warn(final String message, final Throwable ex);

    void error(final String message);

    void error(final String message, final Throwable ex);

    void fatal(final String message);

    void fatal(final String message, final Throwable ex);
}
