package com.google.pso.util;

import com.google.protobuf.MapEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapExample {
    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "one");
        map.put(2, "two");

        System.out.println(map);

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(String.format("Key is %d, Value is %s",
                    entry.getKey(), entry.getValue()));
        }

        Map<Integer, String> output = map.entrySet().stream()
                .filter(entry -> entry.getKey() == 1)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        System.out.println(output);
    }
}
