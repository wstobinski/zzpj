package com.example.fileMismatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileEqualsChecker {

    boolean isFileEquals(Path path1, Path path2) throws IOException {
        String path1Content = Files.readString(path1);
        String path2Content = Files.readString(path2);
        return path1Content.equals(path2Content);
    }
}
