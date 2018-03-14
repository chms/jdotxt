package com.todotxt.todotxttouch.task.sorter;

import com.todotxt.todotxttouch.task.Task;

import java.util.Date;
import java.util.List;

public enum Sorters {
    ID ("") {
        @Override
        public Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    if (t1 == null || t2 == null) {
                        throw new NullPointerException(
                                "Null task passed into comparator");
                    }

                    return Long.compare(t1.getId(), t2.getId()) * (ascending ? 1 : -1);
                }
            };
        }
    },
    PRIORITY ("Priority") {
        @Override
        public Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    if (t1 == null || t2 == null) {
                        throw new NullPointerException(
                                "Null task passed into comparator");
                    }

                    if (t1.isCompleted() && t2.isCompleted()) {
                        return 0;
                    }

                    if (t1.isCompleted() || t2.isCompleted()) {
                        return ascending ^ t1.isCompleted() ? -1 : 1;
                    }

                    return t1.getPriority().compareTo(t2.getPriority()) * (ascending ? 1 : -1);
                }
            };
        }
    },
    TEXT ("Task text") {
        @Override
        Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    if (t1 == null || t2 == null) {
                        throw new NullPointerException(
                                "Null task passed into comparator");
                    }

                    return t1.getText().compareToIgnoreCase(t2.getText()) * (ascending ? 1 : -1);
                }
            };
        }
    },
    DATE ("Creation date") {
        @Override
        Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {

                    String d1 = t1.getPrependedDate();
                    String d2 = t2.getPrependedDate();

                    return (d1 == null ? d2 == null ? 0 : -1 : d1.compareTo(d2)) * (ascending ? 1 : -1);
                }
            };
        }
    },
    COMPLETION_DATE("Due date") {
        @Override
        Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    String d1 = t1.getCompletionDate();
                    String d2 = t2.getCompletionDate();
                    return (d1 == null ? d2 == null ? 0 : -1 : d1.compareTo(d2)) * (ascending ? 1 : -1);
                }
            };
        }
    },
    PROJECTS("Projects") {
        @Override
        Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    List<String> p1 = t1.getProjects();
                    List<String> p2 = t2.getProjects();

                    return compareLists(p1, p2) * (ascending ? 1 : -1);
                }
            };
        }
    },
    CONTEXTS("Contexts") {
        @Override
        Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    List<String> p1 = t1.getContexts();
                    List<String> p2 = t2.getContexts();

                    return compareLists(p1, p2) * (ascending ? 1 : -1);
                }
            };
        }
    },
    THRESHOLD_DATE("Threshold date") {
        @Override
        Sorter<Task> get(final boolean ascending) {
            return new GenericSorter<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    Date d1 = t1.getThresholdDate();
                    Date d2 = t2.getThresholdDate();

                    return (d1 == null ? (d2 == null ? 0 : -1) : (d2 == null ? 1 : d1.compareTo(d2))) * (ascending ? 1 : -1);
                }
            };
        }
    }
    ;

    String name;

    public String getName() {
        return name;
    }

    Sorters(String name) {
        this.name = name;
    }

    abstract Sorter<Task> get(final boolean ascending);

    public Sorter<Task> ascending() {return get(true);}
    public Sorter<Task> descending() {return get(false);}

    public static <E extends Comparable> int compareLists(List<E> l1, List<E> l2) {
        if (l1.equals(l2))
            return 0;

        for (int i = 0; i < Math.min(l1.size(), l2.size()); i++) {
            E s1 = l1.get(i);
            E s2 = l2.get(i);
            int res = s1 == null ? (s2 == null ? 0 : -1) : (s2 == null ? 1 : s1.compareTo(s2));
            if (res != 0)
                return res;
        }
        return l1.size() > l2.size() ? 1 : -1;
    }
}
