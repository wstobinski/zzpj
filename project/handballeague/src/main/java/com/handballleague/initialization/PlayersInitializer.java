package com.handballleague.initialization;

import com.google.cloud.vertexai.VertexAI;
import com.handballleague.model.Player;
import com.handballleague.repositories.PlayerRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PlayersInitializer {

    private final VertexAI vertexAi;
    private final PlayerRepository playerRepository;

    private String API_KEY;

    //    @Autowired
    public PlayersInitializer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.vertexAi = new VertexAI("handball-app-425321", "us-central1");
        Dotenv dotenv = Dotenv.load();
        this.API_KEY = dotenv.get("API_KEY");
    }

    public void generatePlayersData(String nationality, int numberOfPlayers) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
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