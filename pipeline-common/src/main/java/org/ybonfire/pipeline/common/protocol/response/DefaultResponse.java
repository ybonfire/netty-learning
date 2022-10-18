package org.ybonfire.pipeline.common.protocol.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;

/**
 * 默认
 *
 * @author Bo.Yuan5
 * @date 2022-07-13 10:26
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class DefaultResponse implements IRemotingResponseBody {
    private String message;
}
