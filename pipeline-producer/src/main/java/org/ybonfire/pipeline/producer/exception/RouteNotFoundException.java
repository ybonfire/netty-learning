package org.ybonfire.pipeline.producer.exception;

/**
 * 未知路由异常
 *
 * @author yuanbo
 * @date 2022-09-20 18:48
 */
public class RouteNotFoundException extends ProducerException {

    public RouteNotFoundException() {}

    public RouteNotFoundException(final String message) {
        super(message);
    }

    public RouteNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RouteNotFoundException(final Throwable cause) {
        super(cause);
    }
}
