package com.handballleague.initialization;

import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class TeamsInitializer {

    @Value("${api.key}")
    private String apiKey;

    @PostConstruct
    public void fetchAndFillData() {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("v1.handball.api-sports.io")
                .addPathSegment("teams")
                .addQueryParameter("league", "78")
                .addQueryParameter("season", "2023")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-apisports-key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TeamsInitializer initializer = new TeamsInitializer();
        initializer.fetchAndFillData();
    }
}