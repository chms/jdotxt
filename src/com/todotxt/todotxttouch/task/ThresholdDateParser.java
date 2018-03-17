package com.todotxt.todotxttouch.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThresholdDateParser {

    private static final Pattern THRESHOLD_DATE_PATTERN = Pattern.compile("(?:\\s|^)t:(\\d{4}-\\d{1,2}-\\d{1,2})(?:\\s|$)");
    private static final ThresholdDateParser INSTANCE = new ThresholdDateParser();
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private ThresholdDateParser() {
    }

    public static ThresholdDateParser getInstance() {
        return INSTANCE;
    }

    public Date parse(String text) {
        if (text == null)
            return null;
        Matcher m = THRESHOLD_DATE_PATTERN.matcher(text);
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
