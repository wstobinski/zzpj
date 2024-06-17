package com.handballleague.initialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handballleague.exceptions.InitializerException;
import com.handballleague.model.Team;
import com.handballleague.repositories.TeamRepository;
import com.handballleague.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TeamsInitializer {

    @Value("${handball.api.key}")
    private String apiKey;

    private final TeamService teamService;
    private final TeamRepository teamRepository;

    @Autowired
    public TeamsInitializer(TeamService teamService, TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
        this.teamService = teamService;
    }

    public List<Long> addTeamsToDatabase(String jsonData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode teamsNode = rootNode.get("response");

        if (teamsNode == null) {
            throw new InitializerException("Response node not found in JSON data");
        }

        List<Long> addedTeamIds = new ArrayList<>();

        for (JsonNode teamNode : teamsNode) {
            if (addedTeamIds.size() >= 6) {
                break;
            }
            String teamName = teamNode.get("name").asText();

            Team existingTeam = teamRepository.findByTeamName(teamName);
            if (existingTeam == null) {
                Team team = new Team(teamName);
                team = teamService.create(team);
                addedTeamIds.add(team.getUuid());
            }
        }

        return addedTeamIds;
    }



    public List<Long> fetchAndFillData(String leagueId, String season) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v1.handball.api-sports.io/teams?league=" + leagueId + "&season=" + season))
                .header("x-apisports-key", apiKey)
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new InitializerException("HTTP request failed with status code: " + response.statusCode());
            }

            String responseBody = response.body();
            return addTeamsToDatabase(responseBody);

    }
}
