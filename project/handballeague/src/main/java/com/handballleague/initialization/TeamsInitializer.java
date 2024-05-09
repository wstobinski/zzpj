package com.handballleague.initialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handballleague.model.Team;
import com.handballleague.repositories.TeamRepository;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TeamsInitializer {
    String apiKey = "5cd3647c52894e848f3ca0cfa92c186b";

    @Autowired
    private TeamRepository teamRepository;

    public void addTeamsToDatabase(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode teamsNode = rootNode.get("response");

            for (JsonNode teamNode : teamsNode) {
                Long teamId = teamNode.get("id").asLong();
                String teamName = teamNode.get("name").asText();

                if (!teamRepository.existsById(teamId) && teamRepository.findByTeamName(teamName) == null) {
                    Team team = new Team(teamName);

                    teamRepository.save(team);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void fetchAndFillData() {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("v1.handball.api-sports.io")
                .addPathSegment("teams")
                .addQueryParameter("league", "78") // Polish SuperLiga
                .addQueryParameter("season", "2023")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-apisports-key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            addTeamsToDatabase(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}