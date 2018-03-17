package com.chschmid.jdotxt.gui.utils;

import com.todotxt.todotxttouch.task.sorter.Sorters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortUtils {
    public static String writeSort(List<Map.Entry<Sorters, Boolean>> sortList) {
        Map.Entry<Sorters, Boolean> last = sortList.get(sortList.size()-1);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Sorters, Boolean> s: sortList) {
            sb.append(s.getKey().name());
            sb.append(":");
            sb.append(s.getValue().toString());
            if (s != last)
                sb.append("|");
        }
        return sb.toString();
    }

    public static Map<Sorters, Boolean> parseSort(String sortString) {
        Map<Sorters, Boolean> sort = new LinkedHashMap<>();
        String[] sorters = sortString.split("\\|");
        for (String s : sorters) {
            String[] parts = s.split(":");
            Sorters sorter;
            try {
                sorter = Sorters.valueOf(parts[0]);
            } catch (IllegalArgumentException e) {
                continue;
            }
            boolean dir = Boolean.valueOf(parts[1]);
            sort.put(sorter, dir);
        }
        return sort;
    }
}
