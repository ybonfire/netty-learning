package org.ybonfire.netty.common.protocol;

import org.ybonfire.netty.common.model.User;

/**
 * 请求枚举
 *
 * @author Bo.Yuan5
 * @date 2022-06-01 16:25
 */
public enum RequestCommandEnum {
    TEST(RequestCommandCodeConstant.TEST_REQUEST_CODE, "测试", User.class);

    RequestCommandEnum(final int code, String description, Class<?> requestClazz) {
        this.code = code;
        this.description = description;
        this.requestClazz = requestClazz;
    }

    private int code;
    private String description;
    private Class<?> requestClazz;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Class<?> getRequestClazz() {
        return requestClazz;
    }

    public static RequestCommandEnum of(final int code) {
        for (final RequestCommandEnum value : RequestCommandEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }

        return null;
    }
}
