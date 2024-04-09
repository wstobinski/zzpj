package com.example.indent;

public class NoIndentTextFormatter {

    String noIndentText(String text) {
        StringBuilder resultBuilder = new StringBuilder();
        for (String line : text.split("\n")) {
            resultBuilder.append(line.trim()).append("\n");
        }

        return resultBuilder.toString();
    }
}
