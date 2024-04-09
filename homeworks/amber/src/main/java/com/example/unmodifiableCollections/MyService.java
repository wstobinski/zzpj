package com.example.unmodifiableCollections;

import java.util.List;
import java.util.stream.Collectors;

public class MyService {
    Some3rdService some3rdService = new Some3rdService();

    public List<Integer> getUnmodifiableListByCollector() {

        // copy list by stream and Collectors.toUnmodifiableList

        return some3rdService.getList().stream().toList();
    }

    public List<Integer> getUnmodifiableListByCopy() {

        // copy list by static copyOf method

        return List.copyOf(some3rdService.getList());
    }
}
