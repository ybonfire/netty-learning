package org.ybonfire.pipeline.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 通用常量类
 *
 * @author yuanbo
 * @date 2022-10-07 17:57
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommonConstant {
    /**
     * utf8字符集
     */
    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
    /**
     * int类型字节长度
     */
    public static final int INT_BYTE_LENGTH = 4;
    /**
     * long类型字节长度
     */
    public static final int LONG_BYTE_LENGTH = 8;
}
