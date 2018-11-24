package com.todotxt.todotxttouch.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThresholdDateParser {

    private static final Pattern THRESHOLD_DATE_PATTERN = Pattern.compile("(?:\\s|^)t:(\\d{4}-\\d{1,2}-\\d{1,2})(?:\\s|$)");
    private static final Pattern DUE_DATE_PATTERN = Pattern.compile("(?:\\s|^)due:(\\d{4}-\\d{1,2}-\\d{1,2})(?:\\s|$)");
    private static final ThresholdDateParser INSTANCE = new ThresholdDateParser();
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private ThresholdDateParser() {
    }

    public static ThresholdDateParser getInstance() {
        return INSTANCE;
    }

    public Date parseThresholdDate(String text) {
      return this.parse(text, THRESHOLD_DATE_PATTERN);
    }

    public Date parseDueDate(String text) {
      return this.parse(text, DUE_DATE_PATTERN);
    }

    private Date parse(String text, Pattern pattern) {
        if (text == null)
            return null;
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            String possDate = m.group(1).trim();
            try {
                //checking if this is a valid date
                Date d = FORMAT.parse(possDate);
                if (possDate.equals(FORMAT.format(d)))
                    return d;
            } catch (ParseException e) {
                //not a valid date, just continue
            }
        }
        return null;
    }

}
