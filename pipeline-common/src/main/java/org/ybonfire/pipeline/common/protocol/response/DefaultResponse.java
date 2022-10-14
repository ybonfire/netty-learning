package org.ybonfire.pipeline.common.protocol.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;

import java.util.List;

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

    /**
     * 路由上报请求体
     *
     * @author Bo.Yuan5
     * @date 2022-07-01 18:00
     */
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static final class RouteUploadRequest implements IRemotingRequestBody {
        private String address;
        private List<TopicConfigRemotingEntity> topics;
    }
}
