package org.ybonfire.pipeline.common.model;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.util.ExceptionUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息体
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:15
 */
@NoArgsConstructor
@Data
public class Message implements Serializable {
    private String topic;
    private String key;
    private byte[] payload;

    private Message(final Builder builder) {
        this.topic = builder.topic;
        this.key = builder.key;
        this.payload = builder.payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String topic;
        private String key;
        private byte[] payload;

        public Builder topic(final String topic) {
            this.topic = topic;
            return this;
        }

        public Builder key(final String key) {
            this.key = key;
            return this;
        }

        public Builder payload(final byte[] payload) {
            this.payload = payload;
            return this;
        }

        public Message build() {
            if (this.topic == null) {
                throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
            }
            if (this.payload == null) {
                throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
            }

            return new Message(this);
        }
    }
}
