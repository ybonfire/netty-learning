package org.ybonfire.pipeline.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * 消息体
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:15
 */
@NoArgsConstructor
@Data
public class Message implements Serializable {
    private String id = UUID.randomUUID().toString();
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
        private String id;
        private String topic;
        private String key;
        private byte[] payload;

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

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
                throw new IllegalArgumentException();
            }
            if (this.payload == null) {
                throw new IllegalArgumentException();
            }

            return new Message(this);
        }
    }
}
