package com.handballleague.InitializersTests;

import com.handballleague.initialization.PlayersInitializer;
import com.handballleague.model.Player;
import com.handballleague.services.PlayerService;
import com.handballleague.services.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlayersInitializerTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private PlayersInitializer playersInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPlayersToDatabase_shouldAddPlayersWithTeam() {
        List<String> players = Arrays.asList(
                "John,Doe,johndoe@example.com,123456789,10",
                "Jane,Smith,janesmith@example.com,987654321,20"
        );

        when(playerService.create(any(Player.class))).thenAnswer(invocation -> {
            Player player = invocation.getArgument(0);
            player.setUuid(1L);
            return player;
        });

        playersInitializer.addPlayersToDatabase(players, Optional.of(1L));

        verify(playerService, times(2)).create(any(Player.class));
        verify(teamService, times(2)).addPlayerToTeam(eq(1L), anyLong());
    }

    @Test
    void addPlayersToDatabase_shouldAddPlayersWithoutTeam() {
        List<String> players = Arrays.asList(
                "John,Doe,johndoe@example.com,123456789,10",
                "Jane,Smith,janesmith@example.com,987654321,20"
        );

        when(playerService.create(any(Player.class))).thenAnswer(invocation -> {
            Player player = invocation.getArgument(0);
            player.setUuid(1L);
            return player;
        });

        playersInitializer.addPlayersToDatabase(players, Optional.empty());

        verify(playerService, times(2)).create(any(Player.class));
        verify(teamService, times(0)).addPlayerToTeam(anyLong(), anyLong());
    }




}
