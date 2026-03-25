package com.example.util;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class ArrayUtils {

    public static boolean contains(int[] arr, int value) {
        for (int v : arr) if (v == value) return true;
        return false;
    }

    public static int sum(int[] arr) {
        int total = 0;
        for (int v : arr) total += v;
        return total;
    }

    public static double average(int[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        return (double) sum(arr) / arr.length;
    }

    public static int[] reverse(int[] arr) {
        int[] result = Arrays.copyOf(arr, arr.length);
        int left = 0, right = result.length - 1;
        while (left < right) {
            int tmp = result[left];
            result[left] = result[right];
            result[right] = tmp;
            left++;
            right--;
        }
        return result;
    }

    public static int[] sort(int[] arr) {
        int[] result = Arrays.copyOf(arr, arr.length);
        Arrays.sort(result);
        return result;
    }

    public static int[] mergeArrays(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static int[] removeDuplicates(int[] arr) {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        for (int v : arr) set.add(v);
        return set.stream().mapToInt(Integer::intValue).toArray();
    }
}
