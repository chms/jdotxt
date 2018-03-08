package com.todotxt.todotxttouch.task;

public class HiddenFilter implements Filter<Task> {

    @Override
    public boolean apply(Task input) {
        return !input.isHidden();
    }
}
