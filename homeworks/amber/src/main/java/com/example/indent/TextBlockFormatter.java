package com.example.indent;

public class TextBlockFormatter {
    private final int CODE_TEXT_BLOCK_INDENT = 4;

    String formatLinesToCodeBlockIndent(String code) {
        StringBuilder resultBuilder = new StringBuilder();
        for (String line : code.split("\n")) {
            resultBuilder.append(line.indent(CODE_TEXT_BLOCK_INDENT));
        }

        return resultBuilder.toString();
    }
}
