package com.todotxt.todotxttouch.task;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ThresholdDateParserTest {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyy-MM-dd");

    @Test
    public void correctDateParseTest() throws ParseException {
        String task = "Threshold date t:2018-01-01";
        Date expected = FORMAT.parse("2018-01-01");
        Date actual = ThresholdDateParser.getInstance().parse(task);
        assertEquals(expected, actual);
    }

    @Test
    public void noDateParseTest() {
        String task = "Threshold date";
        Date actual = ThresholdDateParser.getInstance().parse(task);
        assertNull(actual);
    }

    @Test
    public void incorrectDateParseTest() {
        String task = "Threshold date t:2018-01-32";
        Date actual = ThresholdDateParser.getInstance().parse(task);
        assertNull(actual);
    }

}
