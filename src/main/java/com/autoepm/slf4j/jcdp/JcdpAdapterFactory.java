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

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JcdpAdapterFactory implements ILoggerFactory {

    private final List<String> validFColors = Arrays.stream(Ansi.FColor.values()).map(Ansi.FColor::toString).collect(Collectors.toList());
    private final List<String> validBColors = Arrays.stream(Ansi.BColor.values()).map(Ansi.BColor::toString).collect(Collectors.toList());

    public static boolean isTsEnabled() {
        return Boolean.valueOf(System.getProperty("jcdp.timestamp.enabled", "false"));
    }


    @Override
    public Logger getLogger(String name) {
        // get basic config from somewhere -- for now I'll hack it into sysprops...
        boolean tsEnabled = Boolean.valueOf(System.getProperty("jcdp.timestamp.enabled", "false"));
        boolean fileEnabled = Boolean.valueOf(System.getProperty("jcdp.file.enabled", "false"));
        JcdpLogLevel enabledLevel = JcdpLogLevel.valueOf(System.getProperty("jcdp.level", "INFO").toUpperCase());

        ColoredPrinter[] printers = new ColoredPrinter[6];
        for (JcdpLogLevel level : JcdpLogLevel.values()) {
            String fConfig = System.getProperty("jcdp." + level.toString() + ".foreground", "BLACK");
            String bConfig = System.getProperty("jcdp." + level.toString() + ".background", "WHITE");
            ColoredPrinter printer = new ColoredPrinter.Builder(enabledLevel.getLevel(), tsEnabled)
                    .foreground(Ansi.FColor.valueOf(fConfig))
                    .background(Ansi.BColor.valueOf(bConfig))
                    .build();
            printer.setLevel(enabledLevel.getLevel());
            printers[level.getLevel()] = printer;
        }
        JcdpAdapter adapter = new JcdpAdapter(enabledLevel, printers);
        if (fileEnabled) {
            adapter.error("Printing to file is not supported yet. " +
                    "Give us a hand at https://github.com/dialex/JCDP ");
            //Printer p = new Printer.Builder(Printer.Types.FILE).level(enabledLevel.getLevel()).build();
            //adapter.setFilePrinter(p);
        }
        return adapter;
    }
}
