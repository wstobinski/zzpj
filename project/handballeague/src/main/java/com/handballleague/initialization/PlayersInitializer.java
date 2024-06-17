package com.handballleague.initialization;

import com.handballleague.exceptions.InitializerException;
import com.handballleague.model.Player;
import com.handballleague.services.PlayerService;
import com.handballleague.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlayersInitializer {


    @Value("${google.api.key}")
    private String apiKey;
    private final PlayerService playerService;
    private final TeamService teamService;

    @Autowired
    public PlayersInitializer(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;

    }

    public void generatePlayersData(String nationality, int numberOfPlayers, Optional<List<Long>> teamIDs) throws Exception {


        if (teamIDs.isPresent()) {
            int size = teamIDs.get().size();
            int totalPlayers = size * numberOfPlayers;
            int callsNeeded = (int) Math.ceil((double) totalPlayers / 36);

            List<String> players = new ArrayList<>();
            for (int call = 0; call < callsNeeded; call++) {
                int playersToGenerate = Math.min(36, totalPlayers - call * 36);
                players.addAll(getPromptResult(nationality, playersToGenerate, Optional.of(size)));
            }

            for (int i = 0; i < size; i++) {
                int start = i * numberOfPlayers;
                int end = Math.min(start + numberOfPlayers, players.size());
                List<String> playersForTeam = players.subList(start, end);
                addPlayersToDatabase(playersForTeam, Optional.of(teamIDs.get().get(i)));
            }


        } else {
            List<String> players = getPromptResult(nationality, numberOfPlayers, Optional.empty());
            addPlayersToDatabase(players, Optional.empty());
        }

    }

    private List<String> getPromptResult(String nationality, int numberOfPlayers, Optional<Integer> numberOfTeams) throws InitializerException, IOException, InterruptedException {
        String jsonInputString;
        if (numberOfTeams.isPresent()) {
            jsonInputString = getFormattedStringPlayersTeams(nationality, numberOfPlayers, numberOfTeams.get());
        } else {
            jsonInputString = getFormattedStringPlayers(nationality, numberOfPlayers);
        }

        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new InitializerException("HTTP request failed with status code: " + response.statusCode());
        }

        return getPlayersFromResponse(response.body());
    }


    private List<String> getPlayersFromResponse(String content) {
        String regex = "\"text\": \"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content.toString());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid response format: 'text' part not found");
        }
        String textContent = matcher.group(1);

        List<String> players = new ArrayList<>(Arrays.asList(textContent.split("\\\\n")));
        players.removeIf(String::isEmpty);

        return players;
    }


    private static String getFormattedStringPlayers(String nationality, int numberOfPlayers) {
        var text = String.format("come up with data for %d  man handball players with %s names. For each of them give" +
                "separated by comma first name, last name, email, phone number (9 digits without country code), pitch number (any number between 0 and 99)." +
                "List this players in separate lines, don't write anything else", numberOfPlayers, nationality);

        return String.format("{ \"contents\":[ { \"parts\":[{\"text\": \"%s\"}]} ]}", text);
    }


    private static String getFormattedStringPlayersTeams(String nationality, int numberOfPlayers, int numberOfTeams) {
        var text = String.format("come up with data for %d man handball players with %s names for each of the %d teams. For each of them give" +
                " separated by comma first name, last name, email, phone number (9 digits without country code), pitch number (any number between 0 and 99)." +
                " Within the same team players can't have same pitch number, but players from two different teams can have." +
                " List these players in separate lines, don't write anything else", numberOfPlayers, nationality, numberOfTeams);

        return String.format("{ \"contents\":[ { \"parts\":[{\"text\": \"%s\"}]} ]}", text);
    }

    private void addPlayersToDatabase(List<String> players, Optional<Long> teamId) {

        int captainIndex = -1;

        if (teamId.isPresent()) {
            captainIndex = (int) (Math.random() * players.size());
        }

        for (int i = 0; i < players.size(); i++) {
            String[] playerData = players.get(i).split(",");
            String firstName = playerData[0];
            String lastName = playerData[1];
            String email = playerData[2];
            String phoneNumber = playerData[3];
            int pitchNumber = Integer.parseInt(playerData[4].trim());
            boolean isCaptain = (captainIndex == i);

            Player newPlayer = new Player(firstName, lastName, phoneNumber, pitchNumber, isCaptain, false);
            newPlayer.setEmail(email);

            Player newPlayer1 = playerService.create(newPlayer);

            if (teamId.isPresent()) {
                teamService.addPlayerToTeam(teamId.get(), newPlayer1.getUuid());
            }

        }
    }
}