package com.handballleague.ocrTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

public class OcrTests {

    @Test
    public void detectText() throws IOException {
        // Replace with your API key
        String apiKey = "AIzaSyC-NNAkvMi7dciKwXMzIlvvJmXB-Y8TE-c";

        // Replace with your image file path
        String filePath = "C:/Users/talla/Desktop/test.jpg";
        byte[] imageBytes = loadImage(filePath);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Prepare the JSON request payload
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestPayload = mapper.createObjectNode();
        ObjectNode image = mapper.createObjectNode();
        image.put("content", base64Image);
        ObjectNode feature = mapper.createObjectNode();
        feature.put("type", "TEXT_DETECTION");
        ObjectNode request = mapper.createObjectNode();
        request.set("image", image);
        request.set("features", mapper.createArrayNode().add(feature));
        requestPayload.set("requests", mapper.createArrayNode().add(request));

        // Send the request to the Vision API
        URL url = new URL("https://vision.googleapis.com/v1/images:annotate?key=" + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(requestPayload));
            os.flush();
        }

        // Read the response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println("Response: " + response);
            }
        } else {
            try (Scanner scanner = new Scanner(connection.getErrorStream())) {
                String errorResponse = scanner.useDelimiter("\\A").next();
                System.err.println("Error response: " + errorResponse);
            }
        }
    }

    private static byte[] loadImage(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return fis.readAllBytes();
        }
    }
}
