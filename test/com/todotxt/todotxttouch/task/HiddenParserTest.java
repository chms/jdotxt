package com.todotxt.todotxttouch.task;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class HiddenParserTest {

    @Test
    public void hiddenTest() {
        String task = "Hidden task h:1";
        boolean hidden = HiddenParser.getInstance().parse(task);
        assertTrue(hidden);
    }

    @Test
    public void notHiddenTest() {
        String task = "Not hidden task";
        boolean hidden = HiddenParser.getInstance().parse(task);
        assertFalse(hidden);
    }

    @Test
    public void hiddenZeroTest() {
        String task = "Hidden zero task h:0";
        boolean hidden = HiddenParser.getInstance().parse(task);
        assertFalse(hidden);
    }
}
