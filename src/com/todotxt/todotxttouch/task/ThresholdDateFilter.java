package com.todotxt.todotxttouch.task;

import java.util.Date;

public class ThresholdDateFilter implements Filter<Task> {

    @Override
    public boolean apply(Task input) {
        Date thresholdDate = input.getThresholdDate();
        if (thresholdDate == null) {
            return true;
        }
        return thresholdDate.getTime() <= System.currentTimeMillis();
    }
}
