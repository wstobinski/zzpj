package com.handballleague.InitializersTests;

import com.handballleague.initialization.TeamsInitializer;
import com.handballleague.model.Team;
import com.handballleague.repositories.TeamRepository;
import com.handballleague.services.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TeamsInitializerTests {

    @Mock
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private TeamsInitializer teamsInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTeamsToDatabase_shouldAddNewTeams() throws IOException {
        String jsonData = "{ \"response\": [ { \"name\": \"Team1\" }, { \"name\": \"Team2\" }, { \"name\": \"Team3\" } ] }";

        when(teamRepository.findByTeamName(anyString())).thenReturn(null);
        when(teamService.create(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setUuid(1L);
            return team;
        });

        var result = teamsInitializer.addTeamsToDatabase(jsonData);

        assertEquals(3, result.size());
        verify(teamRepository, times(3)).findByTeamName(anyString());
        verify(teamService, times(3)).create(any(Team.class));
    }

    @Test
    void addTeamsToDatabase_shouldNotAddExistingTeams() throws IOException {
        String jsonData = "{ \"response\": [ { \"name\": \"Team1\" }, { \"name\": \"Team2\" }, { \"name\": \"Team3\" } ] }";

        when(teamRepository.findByTeamName(anyString())).thenReturn(new Team("ExistingTeam"));

        var result = teamsInitializer.addTeamsToDatabase(jsonData);

        assertEquals(0, result.size());
        verify(teamRepository, times(3)).findByTeamName(anyString());
        verify(teamService, times(0)).create(any(Team.class));
    }

    @Test
    void addTeamsToDatabase_shouldThrowExceptionWhenJsonIsInvalid() {
        String invalidJsonData = "{ \"invalid\": \"data\" }";

        Exception exception = assertThrows(IOException.class, () -> {
            teamsInitializer.addTeamsToDatabase(invalidJsonData);
        });

        String expectedMessage = "Invalid JSON format: response node not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void addTeamsToDatabase_shouldStopAfterAddingSixTeams() throws IOException {
        String jsonData = "{ \"response\": [ " +
                "{ \"name\": \"Team1\" }, { \"name\": \"Team2\" }, { \"name\": \"Team3\" }, " +
                "{ \"name\": \"Team4\" }, { \"name\": \"Team5\" }, { \"name\": \"Team6\" }, " +
                "{ \"name\": \"Team7\" } ] }";

        when(teamRepository.findByTeamName(anyString())).thenReturn(null);
        when(teamService.create(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setUuid(1L);
            return team;
        });

        var result = teamsInitializer.addTeamsToDatabase(jsonData);

        assertEquals(6, result.size());
        verify(teamRepository, times(6)).findByTeamName(anyString());
        verify(teamService, times(6)).create(any(Team.class));
    }
}
