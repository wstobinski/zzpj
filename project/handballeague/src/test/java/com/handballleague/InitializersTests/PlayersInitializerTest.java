package com.handballleague.InitializersTests;

import com.handballleague.initialization.PlayersInitializer;
import com.handballleague.initialization.TeamsInitializer;
import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayersInitializerTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private PlayersInitializer playersInitializer;

    @Test
    public void testAddPlayersToDatabase() throws Exception {
        List<String> players = Arrays.asList(
                "John,Doe,john.doe@example.com,123456789,10,yes",
                "Jane,Doe,jane.doe@example.com,987654321,20,no"
        );

        playersInitializer.addPlayersToDatabase(players);

        verify(playerRepository, times(2)).save(any(Player.class));
    }



}