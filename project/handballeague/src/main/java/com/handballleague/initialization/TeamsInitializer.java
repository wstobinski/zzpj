package com.handballleague.initialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handballleague.model.Team;
import com.handballleague.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TeamsInitializer {
    private final String apiKey = "5cd3647c52894e848f3ca0cfa92c186b";

    @Autowired
    private TeamRepository teamRepository;

    public void addTeamsToDatabase(String jsonData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode teamsNode = rootNode.get("response");

        if (teamsNode == null) {
            throw new IOException("Invalid JSON format: response node not found");
        }

        for (JsonNode teamNode : teamsNode) {
            Long teamId = teamNode.get("id").asLong();
            String teamName = teamNode.get("name").asText();

            if (!teamRepository.existsById(teamId) && teamRepository.findByTeamName(teamName) == null) {
                Team team = new Team(teamName);
                teamRepository.save(team);
            }
        }
    }

    public void fetchAndFillData(String leagueId, String season) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v1.handball.api-sports.io/teams?league=" + leagueId + "&season=" + season))
                .header("x-apisports-key", apiKey)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("HTTP request failed with status code: " + response.statusCode());
            }

            String responseBody = response.body();
            addTeamsToDatabase(responseBody);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
}

}
