package org.ybonfire.netty.common.protocol;

/**
 * 请求枚举
 *
 * @author Bo.Yuan5
 * @date 2022-06-01 16:25
 */
public enum RequestCommandEnum {
    TEST(RequestCommandCodeConstant.TEST_REQUEST_CODE, "测试");

    RequestCommandEnum(final int code, String description) {
        this.code = code;
        this.description = description;
    }

    private int code;
    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
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
