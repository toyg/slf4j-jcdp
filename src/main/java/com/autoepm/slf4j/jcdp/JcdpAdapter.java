/*
 * Copyright (c) 2019 Giacomo Lacava, TarGLet Limited
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.autoepm.slf4j.jcdp;

import com.diogonunes.jcdp.bw.Printer;
import com.diogonunes.jcdp.color.ColoredPrinter;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * This is the adapter that SLF4J will instantiate and use.
 * It extends {@link org.slf4j.helpers.MarkerIgnoringBase} because
 * JCDP has no concept of Markers.
 */
public class JcdpAdapter extends MarkerIgnoringBase {

    final static String FQCN = JcdpAdapter.class.getName();
    private final ColoredPrinter[] printers;
    private final JcdpLogLevel logLevel;
    private boolean fileEnabled = false;
    private Printer filePrinter;

    /** Constructor. One can provide an arbitrary number of printers,
     * up to the maximum number of different levels in {@link JcdpLogLevel}.
     * The order in which they are provided will match the order of levels.
     * Any missing printer will be created as a default one.
     *
     * @param level {@link JcdpLogLevel} the logger will be set to
     * @param printer {@link com.diogonunes.jcdp.color.ColoredPrinter} instances,
     *                                                                in order matching log levels.
     */
    JcdpAdapter(JcdpLogLevel level, ColoredPrinter... printer) {
        this.printers = new ColoredPrinter[6];
        // first slot is empty because level 0 would print all.
        // could reuse for file printer at some point
        for (int i = 1; i < this.printers.length; i++) {
            if (printer.length >= i + 1) {
                this.printers[i] = printer[i];
            } else {
                this.printers[i] = new ColoredPrinter.Builder(
                        level.getLevel(), JcdpAdapterFactory.isTsEnabled()
                ).build();
                this.printers[i].setLevel(level.getLevel());
            }
        }
        logLevel = level;
    }

    /** support for File Printer is currently not in JCDP but it's planned...
     *
     * @param printer {@link com.diogonunes.jcdp.bw.Printer} instance wired to file
     */
    public void setFilePrinter(Printer printer) {
        this.filePrinter = printer;
        fileEnabled = true;
    }

    /** pick the printer for a given level
     *
     * @param level {@link JcdpLogLevel}
     * @return {@link com.diogonunes.jcdp.color.ColoredPrinter} instance
     */
    // kept only pkg-private so it can be tested
    ColoredPrinter getPrinter(JcdpLogLevel level) {
        return this.printers[level.getLevel()];
    }

    /** log an actual message at the specified level
     *
     * @param msg {@link String}
     * @param level {@link JcdpLogLevel}
     */
    private void log(String msg, JcdpLogLevel level) {
        getPrinter(level).debugPrintln(msg, level.getLevel());
        if (fileEnabled) this.filePrinter.debugPrintln(msg, level.getLevel());
    }

    /** log a full traceback of provided exception.
     *
     * @param msg {@link String} message
     * @param t {@link Throwable} exception
     * @param level {@link JcdpLogLevel}
     */
    private void logTraceback(String msg, Throwable t, JcdpLogLevel level) {
        getPrinter(level).debugPrintln(msg, level.getLevel());
        getPrinter(level).debugPrintln("\t\t" + t.toString(), level.getLevel());
        for (StackTraceElement line : t.getStackTrace()) {
            getPrinter(level).debugPrintln("\t\t" + line.toString(), level.getLevel());
        }
        if (fileEnabled) {
            this.filePrinter.debugPrintln(msg, level.getLevel());
            this.filePrinter.debugPrintln("\t\t" + t.toString(), level.getLevel());
            for (StackTraceElement line : t.getStackTrace()) {
                this.filePrinter.debugPrintln("\t\t" + line.toString(), level.getLevel());
            }
        }
    }

    /* --- begin boring SLF4J wrappers --- */

    @Override
    public boolean isTraceEnabled() {
        return this.logLevel.getLevel() >= JcdpLogLevel.TRACE.getLevel();
    }

    @Override
    public void trace(String msg) {
        log(msg, JcdpLogLevel.TRACE);
    }

    @Override
    public void trace(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        log(ft.getMessage(), JcdpLogLevel.TRACE);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        log(ft.getMessage(), JcdpLogLevel.TRACE);
    }

    @Override
    public void trace(String format, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        log(ft.getMessage(), JcdpLogLevel.TRACE);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logTraceback(msg, t, JcdpLogLevel.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logLevel.getLevel() >= JcdpLogLevel.DEBUG.getLevel();
    }

    @Override
    public void debug(String msg) {
        log(msg, JcdpLogLevel.DEBUG);
    }

    @Override
    public void debug(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        log(ft.getMessage(), JcdpLogLevel.DEBUG);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        log(ft.getMessage(), JcdpLogLevel.DEBUG);
    }

    @Override
    public void debug(String format, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        log(ft.getMessage(), JcdpLogLevel.DEBUG);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logTraceback(msg, t, JcdpLogLevel.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logLevel.getLevel() >= JcdpLogLevel.INFO.getLevel();
    }

    @Override
    public void info(String msg) {
        log(msg, JcdpLogLevel.INFO);
    }

    @Override
    public void info(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        log(ft.getMessage(), JcdpLogLevel.INFO);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        log(ft.getMessage(), JcdpLogLevel.INFO);
    }

    @Override
    public void info(String format, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        log(ft.getMessage(), JcdpLogLevel.INFO);
    }

    @Override
    public void info(String msg, Throwable t) {
        logTraceback(msg, t, JcdpLogLevel.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logLevel.getLevel() >= JcdpLogLevel.WARN.getLevel();
    }

    @Override
    public void warn(String msg) {
        log(msg, JcdpLogLevel.WARN);
    }

    @Override
    public void warn(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        log(ft.getMessage(), JcdpLogLevel.WARN);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        log(ft.getMessage(), JcdpLogLevel.WARN);
    }

    @Override
    public void warn(String format, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        log(ft.getMessage(), JcdpLogLevel.WARN);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logTraceback(msg, t, JcdpLogLevel.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logLevel.getLevel() >= JcdpLogLevel.ERROR.getLevel();
    }

    @Override
    public void error(String msg) {
        log(msg, JcdpLogLevel.ERROR);
    }

    @Override
    public void error(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        log(ft.getMessage(), JcdpLogLevel.ERROR);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        log(ft.getMessage(), JcdpLogLevel.ERROR);
    }

    @Override
    public void error(String format, Object... arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        log(ft.getMessage(), JcdpLogLevel.ERROR);
    }

    @Override
    public void error(String msg, Throwable t) {
        logTraceback(msg, t, JcdpLogLevel.ERROR);
    }
}
