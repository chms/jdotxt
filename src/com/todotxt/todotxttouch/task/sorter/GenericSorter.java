package com.todotxt.todotxttouch.task.sorter;

public abstract class GenericSorter<T> implements Sorter<T> {
    @Override
    public Sorter<T> then(Sorter<T> next) {
        return buildGenericSorter(this, next);
    }

    private Sorter<T> buildGenericSorter(final Sorter<T> first, final Sorter<T> next) {
        return new GenericSorter<T>() {
            @Override
            public int compare(T t1, T t2) {
                int res = first.compare(t1, t2);
                if (res != 0)
                    return res;
                return next.compare(t1, t2);
            }
        };
    }
}
