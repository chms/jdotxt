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
    public void correctThresholdDateParseTest() throws ParseException {
        String task = "Threshold date t:2018-01-01";
        Date expected = FORMAT.parse("2018-01-01");
        Date actual = ThresholdDateParser.getInstance().parseThresholdDate(task);
        assertEquals(expected, actual);
    }

    @Test
    public void noDateParseTest() {
        String task = "Threshold date";
        Date thr = ThresholdDateParser.getInstance().parseThresholdDate(task);
        Date due = ThresholdDateParser.getInstance().parseDueDate(task);
        assertNull(thr);
        assertNull(due);
    }

    @Test
    public void incorrectThresholdDateParseTest() {
        String task = "Threshold date t:2018-01-32";
        Date actual = ThresholdDateParser.getInstance().parseThresholdDate(task);
        assertNull(actual);
    }

    @Test
    public void correctDueDateParseTest() throws ParseException {
        String task = "Threshold date due:2018-01-01";
        Date expected = FORMAT.parse("2018-01-01");
        Date actual = ThresholdDateParser.getInstance().parseDueDate(task);
        assertEquals(expected, actual);
    }

    @Test
    public void incorrectDueDateParseTest() {
        String task = "Threshold date due:2018-01-32";
        Date actual = ThresholdDateParser.getInstance().parseDueDate(task);
        assertNull(actual);
    }

    @Test
    public void dueThresholdTogetherParseTest() throws ParseException {
        String task = "Threshold date due:2018-01-30 t:2018-01-29";
        Date due = ThresholdDateParser.getInstance().parseDueDate(task);
        Date thr = ThresholdDateParser.getInstance().parseThresholdDate(task);
        Date due_expected = FORMAT.parse("2018-01-30");
        Date thr_expected = FORMAT.parse("2018-01-29");
        assertEquals(due_expected, due);
        assertEquals(thr_expected, thr);
    }
}
