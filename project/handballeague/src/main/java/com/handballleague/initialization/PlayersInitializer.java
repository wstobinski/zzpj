package com.handballleague.initialization;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.model.Player;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.services.PlayerService;
import com.handballleague.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlayersInitializer {


    @Value("${gemini.api.key}")
    private String apiKey;
    private PlayerService playerService;
    private TeamService teamService;

    @Autowired
    public PlayersInitializer(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;

    }

    public void generatePlayersData(String nationality, int numberOfPlayers, Optional<List<String>> teamIDs) throws Exception {
        if (teamIDs.isPresent()) {
            for (String teamID : teamIDs.get()) {
                List<String> players = getPromptResult(nationality, numberOfPlayers);
                addPlayersToDatabase(players, Optional.of(Long.parseLong(teamID)));
            }
        } else {
            List<String> players = getPromptResult(nationality, numberOfPlayers);
            addPlayersToDatabase(players, Optional.empty());
        }

    }

    private List<String> getPromptResult(String nationality, int numberOfPlayers) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        String jsonInputString = getString(nationality, numberOfPlayers);


        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(jsonInputString.getBytes());
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);


        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();
        System.out.println("Response Content : " + content.toString());
//        String textContent = content.toString().split("\"text\": \"")[1];

        String regex = "\"text\": \"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content.toString());
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid response format: 'text' part not found");
        }
        String textContent = matcher.group(1);

        System.out.println("Text content: " + textContent);

        List<String> players = new ArrayList<>(Arrays.asList(textContent.split("\\\\n")));
        System.out.println("size: " + players.size());
        return players;

    }


    private static String getString(String nationality, int numberOfPlayers) {
        var text = String.format("come up with data for %d  man handball players with %s names. For each of them give" +
                "separated by comma first name, last name, email, phone number (9 digits without country code), pitch number (any number between 0 and 99)," +
                "is capitan (yes or no - only 1 player can be capitan). List this players in separate lines," +
                "don't write anything else", numberOfPlayers, nationality);

        return String.format("{ \"contents\":[ { \"parts\":[{\"text\": \"%s\"}]} ]}", text);
    }

    public void addPlayersToDatabase(List<String> players, Optional<Long> teamId) {

        for (String player : players) {
            System.out.println("Player: " + player);
            String[] playerData = player.split(",");
            String firstName = playerData[0];
            String lastName = playerData[1];
            String email = playerData[2];
            String phoneNumber = playerData[3];
            int pitchNumber = Integer.parseInt(playerData[4].trim());
            boolean isCaptain = playerData[5].equals("yes");
            System.out.println("Player data");
            System.out.println(Arrays.toString(playerData));

            Player newPlayer = new Player(firstName, lastName, phoneNumber, pitchNumber, isCaptain, false);
            newPlayer.setEmail(email);


            Player newPlayer1 = playerService.create(newPlayer);

            if (teamId.isPresent()) {
                teamService.addPlayerToTeam(teamId.get(), newPlayer1.getUuid());
            }
        }
    }
}