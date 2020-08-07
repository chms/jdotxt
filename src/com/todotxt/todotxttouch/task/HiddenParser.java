package com.todotxt.todotxttouch.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HiddenParser {

    private static HiddenParser INSTANCE = new HiddenParser();

    private final Pattern p = Pattern.compile("(?:\\s|^)h:([0-9]+)(?:\\s|$)");

    private HiddenParser() {}

    public static HiddenParser getInstance() {
        return INSTANCE;
    }

    public boolean parse(String text) {
        // return text.contains("h:1");
        //
        String[] res = null;

        Matcher m = p.matcher(text);
        if (m.find()) {
            if (!m.group(1).equals("0")) {
                return true;
            }
        }
        return false;
    }
}
