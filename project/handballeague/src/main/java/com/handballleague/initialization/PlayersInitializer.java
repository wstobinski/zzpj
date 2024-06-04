package com.handballleague.initialization;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
public class PlayersInitializer {

    private final VertexAI vertexAi;

    public PlayersInitializer() {
        this.vertexAi = new VertexAI("handball-app-425321", "us-central1");
    }

    public void generatePlayersData(String nationality, int numberOfPlayers) {
        GenerationConfig generationConfig =
                GenerationConfig.newBuilder()
                        .setMaxOutputTokens(2048)
                        .setTemperature(0.9F)
                        .setTopP(1F)
                        .build();
        GenerativeModel model =
                new GenerativeModel.Builder()
                        .setModelName("gemini-1.0-pro-001")
                        .setVertexAi(vertexAi)
                        .setGenerationConfig(generationConfig)
                        .build();

        var text1 = String.format("come up with data for %d handball players with %s names. For each of them give" +
                " separated by comma first name, last name, phone number (9 digits without country code), pitch number (any number between 0 and 99)," +
                " is capitan (yes or no - only 1 player can be capitan). List this players in separate lines, numerate" +
                " them, don't write anything else", numberOfPlayers, nationality);


        var content = ContentMaker.fromMultiModalData(text1);
        ResponseStream<GenerateContentResponse> responseStream = null;
        try {
            responseStream = model.generateContentStream(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder allContent = new StringBuilder();
        responseStream.stream().forEach(response -> {
            response.getCandidatesList().forEach(candidate -> {
                candidate.getContent().getPartsList().forEach(part -> {
                    allContent.append(part.getText());
                });
            });
        });
        System.out.println(allContent.toString());
    }

    public static void main(String[] args) {
        PlayersInitializer playersInitializer = new PlayersInitializer();
        playersInitializer.generatePlayersData("Russian", 5);
    }
}