package com.example.util;

import java.util.*;

public class CollectionUtils {

    public static boolean isNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static <T> T getFirst(List<T> list) {
        if (isNullOrEmpty(list)) return null;
        return list.get(0);
    }

    public static <T> T getLast(List<T> list) {
        if (isNullOrEmpty(list)) return null;
        return list.get(list.size() - 1);
    }

    public static <T> List<List<T>> partition(List<T> list, int batchSize) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            result.add(new ArrayList<>(list.subList(i, Math.min(i + batchSize, list.size()))));
        }
        return result;
    }

    public static <A, B> List<Map.Entry<A, B>> zip(List<A> listA, List<B> listB) {
        List<Map.Entry<A, B>> result = new ArrayList<>();
        int size = Math.min(listA.size(), listB.size());
        for (int i = 0; i < size; i++) {
            result.add(Map.entry(listA.get(i), listB.get(i)));
        }
        return result;
    }

    public static <T> List<T> flatten(List<List<T>> nestedList) {
        List<T> result = new ArrayList<>();
        for (List<T> inner : nestedList) result.addAll(inner);
        return result;
    }
}
