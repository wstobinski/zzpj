package com.example.collectionFactoryMethods;

import java.util.Set;

public class ImageConverter {
    private final String JPG_FILE_KEY = "jpg";
    private final String PNG_FILE_KEY = "png";
    private final String BMP_FILE_KEY = "bmp";


    public Set<String> getAvailableFileKeys() {
        // TODO: implement here

        return Set.of(JPG_FILE_KEY, PNG_FILE_KEY, BMP_FILE_KEY);
    }
}
