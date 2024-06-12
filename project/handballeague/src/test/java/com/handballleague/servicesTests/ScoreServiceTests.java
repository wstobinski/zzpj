package com.handballleague.servicesTests;

import com.handballleague.model.Score;
import com.handballleague.model.Team;
import com.handballleague.repositories.ScoreRepository;
import com.handballleague.services.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ScoreServiceTests {

    @InjectMocks
    private ScoreService scoreService;

    @Mock
    private ScoreRepository scoreRepository;

    private Team team1;
    private Team team2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        team1 = new Team("Team 1");
        team1.setUuid(1L);
        team2 = new Team("Team 2");
        team2.setUuid(2L);
    }

    @Test
    public void testGetMatchesAndWinners_Team1WinsAll() {
        Score score1 = new Score();
        score1.setGoals(3);
        score1.setLostGoals(1);
        Score score2 = new Score();
        score2.setGoals(4);
        score2.setLostGoals(2);

        List<Score> scores = Arrays.asList(score1, score2);

        when(scoreRepository.findScoresByTeams(team1.getUuid(), team2.getUuid())).thenReturn(scores);


        Map<String, Double> matchResults = scoreService.getWinningChances(team1, team2);
        assertEquals(1.0, matchResults.get(team1.getTeamName()));
        assertEquals(0.0, matchResults.get(team2.getTeamName()));
    }

    @Test
    public void testGetMatchesAndWinners_Team2WinsAll() {
        Score score1 = new Score();
        score1.setGoals(1);
        score1.setLostGoals(3);
        Score score2 = new Score();
        score2.setGoals(2);
        score2.setLostGoals(4);
        List<Score> scores = Arrays.asList(score1, score2);

        when(scoreRepository.findScoresByTeams(team1.getUuid(), team2.getUuid())).thenReturn(scores);


        Map<String, Double> matchResults = scoreService.getWinningChances(team1, team2);
        assertEquals(0.0, matchResults.get(team1.getTeamName()));
        assertEquals(1.0, matchResults.get(team2.getTeamName()));
    }

    @Test
    public void testGetMatchesAndWinners_AllMatchesDraw() {
        Score score1 = new Score();
        score1.setGoals(2);
        score1.setLostGoals(2);
        Score score2 = new Score();
        score2.setGoals(3);
        score2.setLostGoals(3);
        List<Score> scores = Arrays.asList(score1, score2);

        when(scoreRepository.findScoresByTeams(team1.getUuid(), team2.getUuid())).thenReturn(scores);


        Map<String, Double> matchResults = scoreService.getWinningChances(team1, team2);
        assertEquals(0.5, matchResults.get(team1.getTeamName()));
        assertEquals(0.5, matchResults.get(team2.getTeamName()));
    }
}