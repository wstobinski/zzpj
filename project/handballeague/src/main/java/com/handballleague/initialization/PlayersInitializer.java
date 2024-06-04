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
import com.handballleague.model.Player;
import com.handballleague.repositories.PlayerRepository;
import com.jayway.jsonpath.internal.filter.ValueNodes;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final PlayerRepository playerRepository;
    @Autowired
    public PlayersInitializer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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
                " separated by comma first name, last name, email, phone number (9 digits without country code), pitch number (any number between 0 and 99)," +
                " is capitan (yes or no - only 1 player can be capitan). List this players in separate lines, numerate" +
                " them, don't write anything else", numberOfPlayers, nationality);


        var content = ContentMaker.fromMultiModalData(text1);
        ResponseStream<GenerateContentResponse> responseStream = null;
        try {
            responseStream = model.generateContentStream(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder playerData = new StringBuilder();
        responseStream.stream().forEach(response -> {
            response.getCandidatesList().forEach(candidate -> {
                candidate.getContent().getPartsList().forEach(part -> {
                    playerData.append(part.getText());
                });
            });
        });
        List<String> players = new ArrayList<>(Arrays.asList(playerData.toString().split("\n")));

        try {
            addPlayersToDatabase(players);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPlayersToDatabase(List<String> players) throws IOException {
        for (String player : players) {
            String[] playerData = player.split(",");
            String firstName = playerData[0];
            String lastName = playerData[1];
            String email = playerData[2];
            String phoneNumber = playerData[3];
            int pitchNumber = Integer.parseInt(playerData[4]);
            boolean isCaptain = playerData[5].equals("yes");

            if (playerRepository.findByEmail(email) == null) {
                Player newPlayer = new Player(firstName, lastName, phoneNumber, pitchNumber, isCaptain, false);
                playerRepository.save(newPlayer);
            }
        }
    }

}