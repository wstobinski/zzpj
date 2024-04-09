package com.example.stringMethods;

import java.util.List;
import java.util.stream.Collectors;

public class StringService {

    List<String> getOnlyNotBlankStrings(List<String> input) {
        // TODO: implement here

        return input.stream().filter(s -> !s.isBlank()).toList();
    }

    List<String> getStrippedTextLines(String text) {
        List<String> lines = text.lines().collect(Collectors.toList());
        lines.replaceAll(String::strip);
        return lines;
    }

    List<String> extendFoundStringByRepeatSomeTimes(List<String> list, String searchedText, int nTimesRepeat) {
        return list.stream()
                .map(element -> element.equals(searchedText) ? element.repeat(nTimesRepeat) : element)
                .toList();
    }
}
