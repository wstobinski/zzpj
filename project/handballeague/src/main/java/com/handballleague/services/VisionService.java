package com.handballleague.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.handballleague.DTO.GoogleCloudVisionDTO;
import com.handballleague.exceptions.ImageProcessingException;
import com.handballleague.util.HttpUrlConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Service
public class VisionService {

    @Value("${google.api.key}")
    private String apiKey;

    private final HttpUrlConnectionFactory connectionFactory;


    @Autowired
    public VisionService(HttpUrlConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public String detectText(String base64Image) throws IOException {

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

        URL url = URI.create("https://vision.googleapis.com/v1/images:annotate?key=" + apiKey).toURL();
        HttpURLConnection connection = connectionFactory.createHttpURLConnection(url);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(requestPayload));
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                String response = scanner.useDelimiter("\\A").next();
                GoogleCloudVisionDTO responseInDTO = mapper.readValue(response, GoogleCloudVisionDTO.class);
                return responseInDTO.getResponsesList().getFirst().getTextAnnotations().getFirst().getDescription();
            } catch (NoSuchElementException e) {
                throw new ImageProcessingException("Text not provided in the response");
            }
        } else {
            try (Scanner scanner = new Scanner(connection.getErrorStream())) {
                String errorResponse = scanner.useDelimiter("\\A").next();
                System.err.println("Error response: " + errorResponse);
                throw new ImageProcessingException(errorResponse);
            }
        }

    }

}
