package com.handballleague.initialization;

import com.handballleague.model.Player;
import com.handballleague.services.PlayerService;
import com.handballleague.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public void generatePlayersData(String nationality, int numberOfPlayers, Optional<List<Long>> teamIDs) throws Exception {

        System.out.println("Generating players data");
        for (Long teamID : teamIDs.orElse(new ArrayList<>())) {
            System.out.println("TeamID: " + teamID);
        }


        if (teamIDs.isPresent()) {
            for (Long teamID : teamIDs.get()) {
                List<String> players = getPromptResult(nationality, numberOfPlayers);
                addPlayersToDatabase(players, Optional.of(teamID));
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

        String jsonInputString = getFormattedStringPlayers(nationality, numberOfPlayers);


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

    public void addPlayersToDatabase(List<String> players, Optional<Long> teamId) {

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


            teamId.ifPresent(aLong -> teamService.addPlayerToTeam(aLong, newPlayer1.getUuid()));
        }
    }
}