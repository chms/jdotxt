package com.todotxt.todotxttouch.task.sorter;

import com.todotxt.todotxttouch.task.Task;

import static com.todotxt.todotxttouch.task.sorter.Sorters.*;

public class PredefinedSorters {
    public static final Sorter<Task> DEFAULT = PRIORITY.ascending().then(ID.ascending());
    public static final Sorter<Task> TEXT_ASC = TEXT.ascending().then(ID.ascending());
    public static final Sorter<Task> DATE_ASC = DATE.ascending().then(ID.ascending());
    public static final Sorter<Task> DATE_DESC = DATE.descending().then(ID.ascending());
}
