package org.ybonfire.pipeline.common.logger.impl;

import org.ybonfire.pipeline.common.logger.IInternalLogger;

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
        OUT_PRINTER.println(message);
    }

    @Override
    public void trace(String message, Throwable ex) {
        OUT_PRINTER.println(message);
    }

    @Override
    public void debug(String message) {
        OUT_PRINTER.println(message);
    }

    @Override
    public void debug(String message, Throwable ex) {
        OUT_PRINTER.println(message);
    }

    @Override
    public void info(String message) {
        OUT_PRINTER.println(message);
    }

    @Override
    public void info(String message, Throwable ex) {
        OUT_PRINTER.println(message);
    }

    @Override
    public void warn(String message) {
        ERR_PRINTER.println(message);
    }

    @Override
    public void warn(String message, Throwable ex) {
        ERR_PRINTER.println(message);
    }

    @Override
    public void error(String message) {
        ERR_PRINTER.println(message);
    }

    @Override
    public void error(String message, Throwable ex) {
        ERR_PRINTER.println(message);
    }

    @Override
    public void fatal(String message) {
        ERR_PRINTER.println(message);
    }

    @Override
    public void fatal(String message, Throwable ex) {
        ERR_PRINTER.println(message);
    }
}
