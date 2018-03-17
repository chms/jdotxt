package com.todotxt.todotxttouch.task;

public class HiddenParser {

    private static HiddenParser INSTANCE = new HiddenParser();

    private HiddenParser() {
    }

    public static HiddenParser getInstance() {
        return INSTANCE;
    }

    public boolean parse(String text) {
        return text.contains("h:1");
    }
}
