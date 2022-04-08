package com.example.todo.util;

import android.util.Pair;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ListUtils {

    public static <T>  boolean checkIfTheSame(List<T> list1, List<T> list2){
        if(list1.size() != list2.size()){
            return false;
        }
        return IntStream
                .range(0, Math.min(list1.size(), list2.size()))
                .mapToObj(i -> new Pair<T, T>(list1.get(i), list2.get(i) ))
                .map(pair -> pair.first.equals(pair.second))
                .allMatch(Predicate.isEqual(true));
    }
}
