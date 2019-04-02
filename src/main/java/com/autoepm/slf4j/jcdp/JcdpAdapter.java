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

public class JcdpAdapter extends MarkerIgnoringBase {

    final static String FQCN = JcdpAdapter.class.getName();
    private final ColoredPrinter[] printers;
    private final JcdpLogLevel logLevel;
    private boolean fileEnabled = false;
    private Printer filePrinter;

    JcdpAdapter(JcdpLogLevel level, ColoredPrinter... printer) {
        this.printers = new ColoredPrinter[5];
        for (int i = 0; i < this.printers.length; i++) {
            if (printer.length >= i + 1) {
                this.printers[i] = printer[i];
            } else {
                this.printers[i] = new ColoredPrinter.Builder(i, true).build();
            }
        }
        logLevel = level;
    }

    public void setFilePrinter(Printer printer) {
        this.filePrinter = printer;
        fileEnabled = true;
    }

    private ColoredPrinter getPrinter(JcdpLogLevel level) {
        return this.printers[level.getLevel()];
    }

    private void log(String msg, JcdpLogLevel level) {
        getPrinter(level).debugPrintln(msg, level.getLevel());
        if (fileEnabled) this.filePrinter.debugPrintln(msg, level.getLevel());
    }

    private void logTraceback(String msg, Throwable t, JcdpLogLevel level) {
        getPrinter(level).debugPrintln(msg, level.getLevel());
        getPrinter(level).debugPrint("original exception was: ");
        getPrinter(level).debugPrintln(t.getMessage(), level.getLevel());
        if (fileEnabled) {
            this.filePrinter.debugPrintln(msg, level.getLevel());
            this.filePrinter.debugPrint("original exception was: ");
            this.filePrinter.debugPrintln(t.getMessage(), level.getLevel());
        }
    }


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
