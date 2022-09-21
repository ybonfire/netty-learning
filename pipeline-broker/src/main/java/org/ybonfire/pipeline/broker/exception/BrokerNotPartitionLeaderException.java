package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * Broker非PartitionLeader时消息投递异常
 *
 * @author yuanbo
 * @date 2022-09-14 14:25
 */
public class BrokerNotPartitionLeaderException extends ServerException {
    private final ResponseEnum responseType = ResponseEnum.SERVER_ROLE_NOT_SUPPORTED;

    public BrokerNotPartitionLeaderException() {}

    public BrokerNotPartitionLeaderException(final String message) {
        super(message);
    }

    public BrokerNotPartitionLeaderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BrokerNotPartitionLeaderException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return responseType;
    }
}
