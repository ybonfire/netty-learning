package org.ybonfire.netty.common.logger.impl;

import org.ybonfire.netty.common.logger.IInternalLogger;

import java.io.PrintStream;

/**
 * 简易内部日志实现
 *
 * @author Bo.Yuan5
 * @date 2022-06-01 15:26
 */
public class SimpleInternalLogger implements IInternalLogger {
    private static final PrintStream OUT_PRINTER = System.out;
    private static final PrintStream ERR_PRINTER = System.err;

    @Override
    public void trace(String message) {
        System.out.println(message);
    }

    @Override
    public void trace(String message, Throwable ex) {
        System.out.println(message);
    }

    @Override
    public void debug(String message) {
        System.out.println(message);
    }

    @Override
    public void debug(String message, Throwable ex) {
        System.out.println(message);
    }

    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void info(String message, Throwable ex) {
        System.out.println(message);
    }

    @Override
    public void warn(String message) {
        System.out.println(message);
    }

    @Override
    public void warn(String message, Throwable ex) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message, Throwable ex) {
        System.out.println(message);
    }

    @Override
    public void fatal(String message) {
        System.out.println(message);
    }

    @Override
    public void fatal(String message, Throwable ex) {
        System.out.println(message);
    }
}
