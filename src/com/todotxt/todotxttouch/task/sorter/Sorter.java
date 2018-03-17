package com.todotxt.todotxttouch.task.sorter;

import java.util.Comparator;

public interface Sorter<T> extends Comparator<T> {

    Sorter<T> then(Sorter<T> next);

}
