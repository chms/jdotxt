package com.todotxt.todotxttouch.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecParser {
    private static final RecParser INSTANCE = new RecParser();
    private final Pattern p = Pattern.compile("rec:(\\+?)([0-9]+)([dwmy])");

    private RecParser() {}

    public static RecParser getInstance() {
        return INSTANCE;
    }

    public String[] parse(String text) {
        String[] res = null;

        Matcher m = p.matcher(text);
        if (m.find()) {
            res = new String[3];
            res[0] = m.group(1);
            res[1] = m.group(2);
            res[2] = m.group(3);
        }

        return res;
    }
}
