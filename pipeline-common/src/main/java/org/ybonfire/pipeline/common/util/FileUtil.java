package org.ybonfire.pipeline.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 文件工具类
 *
 * @author yuanbo
 * @date 2022-09-22 10:27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtil {

    /**
     * 读取指定文件内容
     *
     * @param filename 文件名
     * @return {@link String}
     */
    public static String readFromFile(final String filename) throws IOException {
        return read(new File(filename));
    }

    /**
     * 将字符串写入指定文件
     *
     * @param filename 文件名
     * @param content 内容
     */
    public static void writeToFile(final String filename, final String content) throws IOException {
        write(new File(filename), content);
    }

    /**
     * 读取文件内容
     *
     * @param file 文件
     * @return {@link String}
     */
    public static String read(final File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }

        byte[] data = new byte[(int)file.length()];
        try (final FileInputStream inputStream = new FileInputStream(file)) {
            return inputStream.read(data) == data.length ? new String(data, StandardCharsets.UTF_8) : null;
        }
    }

    /**
     * 将字符串写入指定文件
     *
     * @param file 文件
     * @param content 内容
     * @throws IOException ioexception
     */
    public static void write(final File file, final String content) throws IOException {
        if (file == null) {
            return;
        }

        final File fileParent = file.getParentFile();
        if (fileParent != null) {
            fileParent.mkdirs();
        }
        try (final FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}
