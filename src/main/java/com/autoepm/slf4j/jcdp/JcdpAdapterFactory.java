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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory that SLF4J will call to retrieve a logger.
 */
public class JcdpAdapterFactory implements ILoggerFactory {

    private final List<Ansi.FColor> validFColors = Arrays.stream(Ansi.FColor.values()).collect(Collectors.toList());
    private final List<Ansi.BColor> validBColors = Arrays.stream(Ansi.BColor.values()).collect(Collectors.toList());

    public static boolean isTsEnabled() {
        return Boolean.valueOf(System.getProperty("jcdp.timestamp.enabled", "false"));
    }

    private Properties loadProperties() {
        // get basic config from somewhere -- for now I'll hack it into sysprops...
        Set<String> sysprops = System.getProperties().stringPropertyNames();
        Map<Object, Object> jcdpPropsMap = System.getProperties().entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith("jcdp."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Properties jcdpProps = new Properties();
        jcdpProps.putAll(jcdpPropsMap);
        return jcdpProps;
    }


    @Override
    public Logger getLogger(String name) {
        Properties props = loadProperties(); // reloaded every time so we can change in-flight

        boolean tsEnabled = Boolean.valueOf(props.getProperty("jcdp.timestamp.enabled", "false"));
        boolean fileEnabled = Boolean.valueOf(props.getProperty("jcdp.file.enabled", "false"));
        JcdpLogLevel enabledLevel = JcdpLogLevel.valueOf(props.getProperty("jcdp.level", "INFO").toUpperCase());

        ColoredPrinter[] printers = new ColoredPrinter[6];
        for (JcdpLogLevel level : JcdpLogLevel.values()) {
            // retrieve the codes
            String bConfig = props.getProperty("jcdp." + level.toString() + ".background", "BLACK");
            String fConfig = props.getProperty("jcdp." + level.toString() + ".foreground", "WHITE");
            Object[] colors = new Object[2];
            try {
                colors[0] = Ansi.BColor.valueOf(bConfig);
                colors[1] = Ansi.FColor.valueOf(fConfig);
            } catch (IllegalArgumentException iae) {
                // defaults
                if (colors[0] == null) colors[0] = Ansi.BColor.BLACK;
                if (colors[1] == null) colors[1] = Ansi.FColor.WHITE;
            }
            // build the printer
            ColoredPrinter printer = new ColoredPrinter.Builder(enabledLevel.getLevel(), tsEnabled)
                    .background((Ansi.BColor) colors[0])
                    .foreground((Ansi.FColor) colors[1])
                    .build();
            // ensure level is set - I've seen weird bugs
            printer.setLevel(enabledLevel.getLevel());
            // push where it belongs
            printers[level.getLevel()] = printer;
        }
        // create the adapter
        JcdpAdapter adapter = new JcdpAdapter(enabledLevel, printers);
        // file support, eventually
        if (fileEnabled) {
            adapter.error("Printing to file is not supported yet. " +
                    "Give us a hand at https://github.com/dialex/JCDP ");
            //Printer p = new Printer.Builder(Printer.Types.FILE).level(enabledLevel.getLevel()).build();
            //adapter.setFilePrinter(p);
        }
        return adapter;
    }
}