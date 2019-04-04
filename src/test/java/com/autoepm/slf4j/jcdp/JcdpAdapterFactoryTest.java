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

import com.diogonunes.jcdp.color.api.Ansi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JcdpAdapterFactoryTest {

    private String expectedERRORFormat;
    private String expectedWARNFormat;
    private String expectedINFOFormat;
    private String expectedDEBUGFormat;
    private String expectedTRACEFormat;

    private File tempLog;

    @Before
    public void setUp() throws Exception {
        tempLog = File.createTempFile("test_", "_JcdpAdapterFactoryTest");
        System.out.println(tempLog.getAbsolutePath());
        Properties props = new Properties();
        props.put("jcdp.file.path", tempLog.getAbsolutePath());
        props.put("jcdp.file.enabled", "true");
        props.put("jcdp.timestamp.enabled", "false");
        props.put("jcdp.level", "WARN");
        props.put("jcdp.ERROR.foreground", "WHITE");
        props.put("jcdp.ERROR.background", "RED");
        expectedERRORFormat = "\u001B[;" + Ansi.FColor.WHITE.getCode() + ";" + Ansi.BColor.RED.getCode() + "m";
        props.put("jcdp.WARN.foreground", "BLACK");
        props.put("jcdp.WARN.background", "YELLOW");
        expectedWARNFormat = "\u001B[;" + Ansi.FColor.BLACK.getCode() + ";" + Ansi.BColor.YELLOW.getCode() + "m";
        props.put("jcdp.INFO.foreground", "WHITE");
        props.put("jcdp.INFO.background", "GREEN");
        expectedINFOFormat = "\u001B[;" + Ansi.FColor.WHITE.getCode() + ";" + Ansi.BColor.GREEN.getCode() + "m";
        props.put("jcdp.DEBUG.foreground", "WHITE");
        props.put("jcdp.DEBUG.background", "BLACK");
        expectedDEBUGFormat = "\u001B[;" + Ansi.FColor.WHITE.getCode() + ";" + Ansi.BColor.BLACK.getCode() + "m";
        props.put("jcdp.TRACE.foreground", "MAGENTA");
        props.put("jcdp.TRACE.background", "BLACK");
        expectedTRACEFormat = "\u001B[;" + Ansi.FColor.MAGENTA.getCode() + ";" + Ansi.BColor.BLACK.getCode() + "m";
        System.getProperties().putAll(props);
    }

    @After
    public void tearDown() throws Exception {
        tempLog.delete();
    }

    /**
     * ensure the resulting logger is as expected
     */
    @Test
    public void getLogger() {

        JcdpAdapterFactory factory = new JcdpAdapterFactory();
        JcdpAdapter logger = (JcdpAdapter) factory.getLogger("Something");

        assertFalse(logger.isTraceEnabled());
        assertFalse(logger.isDebugEnabled());
        assertFalse(logger.isInfoEnabled());
        assertTrue(logger.isWarnEnabled());
        assertTrue(logger.isErrorEnabled());
        assertTrue(logger.isFileEnabled());

        assertEquals(logger.getPrinter(JcdpLogLevel.ERROR).generateCode(), expectedERRORFormat);
        assertEquals(logger.getPrinter(JcdpLogLevel.WARN).generateCode(), expectedWARNFormat);
        assertEquals(logger.getPrinter(JcdpLogLevel.INFO).generateCode(), expectedINFOFormat);
        assertEquals(logger.getPrinter(JcdpLogLevel.DEBUG).generateCode(), expectedDEBUGFormat);
        assertEquals(logger.getPrinter(JcdpLogLevel.TRACE).generateCode(), expectedTRACEFormat);


    }

    @Test
    public void getLoggerApiTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // this tests for the existence of methods,
        // so in practice it's an slf4j-api-compliance test rather than a functionality one
        // output should match the system preference, so WARN & ERROR only

        JcdpAdapterFactory factory = new JcdpAdapterFactory();
        Logger logger = factory.getLogger("Something");

        List<String> levels = Arrays.stream(JcdpLogLevel.values()).map(f -> f.toString()).collect(Collectors.toList());
        Class adapterClass = com.autoepm.slf4j.jcdp.JcdpAdapter.class;
        for (String levelName : levels) {
            // trace(msg)
            Method m1 = adapterClass.getDeclaredMethod(levelName.toLowerCase(), new Class[]{String.class});
            Object result1 = m1.invoke(logger, "- test " + levelName + " 1 -");
            //trace(format, obj)
            Method m2 = adapterClass.getDeclaredMethod(levelName.toLowerCase(), new Class[]{String.class, Object.class});
            Object result2 = m2.invoke(logger, "- {} -", "test " + levelName + " 2");
            // trace(format, obj, obj)
            Method m3 = adapterClass.getDeclaredMethod(levelName.toLowerCase(), new Class[]{String.class, Object.class, Object.class});
            Object result3 = m3.invoke(logger, "- {}{} -", "test " + levelName, " 3");
            // trace(format, obj...)
            Method m4 = getVarargsMethod(adapterClass, levelName.toLowerCase());
            Object[] params = {"- ", "test " + levelName, " 4 -"};
            Object result4 = m4.invoke(logger, "{}{}{}", params);
            // trace(msg, exc)
            Method m5 = adapterClass.getDeclaredMethod(levelName.toLowerCase(), new Class[]{String.class, Throwable.class});
            Object result5 = m5.invoke(logger, "test throwable " + levelName,
                    new Exception("test " + levelName + " 4"));
        }
    }

    /**
     * utility to deal with varargs
     */
    private Method getVarargsMethod(Class klass, String methodName) throws NoSuchMethodException {
        Class[] signature = {String.class, Object[].class};
        Method[] methods = klass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                Class[] params = m.getParameterTypes();
                if (params.length == signature.length) {
                    int i;
                    for (i = 0; i < signature.length && params[i].isAssignableFrom(signature[i]); i++) {
                    }
                    if (i == signature.length) {
                        if (params[1].isArray()) return m;
                    }
                }
            }
        }
        throw new NoSuchMethodException(methodName);
    }

}