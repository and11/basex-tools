package com.github.and11.basex.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class FunctionUtilsTest {

    @Test
    public void testFunctionUrlPatternWithArguments(){
        Matcher matcher = FunctionUtils.urlPattern.matcher("xqf:xml:some-namespace#some-name@a1;a2");
        assertTrue(matcher.matches());
        assertEquals(4, matcher.groupCount());
        assertEquals("xml", matcher.group(1));
        assertEquals("some-namespace", matcher.group(2));
        assertEquals("some-name", matcher.group(3));
        assertEquals("a1;a2", matcher.group(4));


    }

    @Test
    public void testFunctionUrlPatternWithoutArguments(){
        Matcher matcher = FunctionUtils.urlPattern.matcher("xqf:xml:some-namespace#some-name");
        assertTrue(matcher.matches());
        assertEquals(4, matcher.groupCount());
        assertEquals("xml", matcher.group(1));
        assertEquals("some-namespace", matcher.group(2));
        assertEquals("some-name", matcher.group(3));
        assertNull(matcher.group(4));
    }
}