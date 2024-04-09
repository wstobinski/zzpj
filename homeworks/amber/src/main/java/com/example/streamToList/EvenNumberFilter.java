package com.example.streamToList;

import java.util.List;

public class EvenNumberFilter {

    List<Integer> getEvenNumbers(List<Integer> allNumbers) {

        return allNumbers.stream().filter(integer -> integer % 2 == 0).toList();
    }
}
