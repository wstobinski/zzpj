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

    public void generatePlayersData(String nationality, int numberOfPlayers) throws Exception {
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

        List<String> players = new ArrayList<>(Arrays.asList(content.toString().split("\n")));

        try {
            addPlayersToDatabase(players);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getString(String nationality, int numberOfPlayers) {
        var text = String.format("come up with data for %d handball players with %s names. For each of them give" +
                " separated by comma first name,last name,email,phone number (9 digits without country code),pitch number (any number between 0 and 99)," +
                "is capitan (yes or no - only 1 player can be capitan). List this players in separate lines, numerate" +
                " them, don't write anything else", numberOfPlayers, nationality);

        return String.format("{ \"contents\":[ { \"parts\":[{\"text\": \"%s\"}]} ]}", text);
    }

    public void addPlayersToDatabase(List<String> players, Optional<Long> teamId) throws Exception {

        for (String player : players) {
            String[] playerData = player.split(",");
            String firstName = playerData[0];
            String lastName = playerData[1];
            String email = playerData[2];
            String phoneNumber = playerData[3];
            int pitchNumber = Integer.parseInt(playerData[4]);
            boolean isCaptain = playerData[5].equals("yes");

            Player newPlayer = new Player(firstName, lastName, phoneNumber, pitchNumber, isCaptain, false);
            Player newPlayer1 = playerService.create(newPlayer);

            if (teamId.isPresent()) {
                teamService.addPlayerToTeam(teamId.get(), newPlayer1.getUuid());
            }
        }
    }
}