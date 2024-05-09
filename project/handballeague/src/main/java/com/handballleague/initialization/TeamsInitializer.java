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

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private TeamRepository teamRepository;

    public void addTeamsToDatabase(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode teamsNode = rootNode.get("response");

            for (JsonNode teamNode : teamsNode) {
                Team team = new Team();
                team.setUuid(teamNode.get("id").asLong());
                team.setTeamName(teamNode.get("name").asText());


                teamRepository.save(team);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PostConstruct
    public void fetchAndFillData() {
        apiKey = "5cd3647c52894e848f3ca0cfa92c186b";
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
            String responseBody = response.body().string();
            System.out.println(responseBody);
            addTeamsToDatabase(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TeamsInitializer initializer = new TeamsInitializer();
        initializer.fetchAndFillData();
    }
}