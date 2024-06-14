package com.handballleague.util;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.Data;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Data
public class SentimentProvider {

    @Value("${sentiment.api.key}")
    private String apiKey;
    private String url;

    public SentimentProvider() {

    }

    public List<Float> getSentiment(String text) {
        this.url = String.format("https://language.googleapis.com/v1/documents:analyzeSentiment?key=%s", this.apiKey);
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Map<String, Object> document = new HashMap<>();
        document.put("type", "PLAIN_TEXT");
        document.put("content", text);
        Map<String, Object> request = new HashMap<>();
        request.put("document", document);

        String sentiment = null;
        String magnitude = null;
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
            JsonHttpContent content = new JsonHttpContent(jsonFactory, request);

            // Make the request
            HttpRequest httpRequest = requestFactory.buildPostRequest(new GenericUrl(url), content);
            HttpResponse response = httpRequest.execute();

            // Output the response
//            System.out.println(response.parseAsString());
//            System.out.println(response.getContent());
            JsonElement jsonElement = JsonParser.parseString(response.parseAsString());
            sentiment = jsonElement.getAsJsonObject().get("documentSentiment").getAsJsonObject().get("score").getAsString();
            magnitude = jsonElement.getAsJsonObject().get("documentSentiment").getAsJsonObject().get("magnitude").getAsString();
//            System.out.println("Sentiment: " + sentiment + " Magnitude: " + magnitude);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return List.of(Float.parseFloat(sentiment), Float.parseFloat(magnitude));
    }
}
