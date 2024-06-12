package com.handballleague.servicesTests;

import com.handballleague.model.Score;
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMatchesAndWinners_Team1WinsAll() {
        Long team1Id = 1L;
        Long team2Id = 2L;
        Score score1 = new Score();
        score1.setGoals(3);
        score1.setLostGoals(1);
        Score score2 = new Score();
        score2.setGoals(4);
        score2.setLostGoals(2);

        List<Score> scores = Arrays.asList(score1, score2);

        when(scoreRepository.findScoresByTeams(team1Id, team2Id)).thenReturn(scores);


        Map<Long, Integer> matchResults = scoreService.getMatchesAndWinners(team1Id, team2Id);
        assertEquals(2, matchResults.get(team1Id));
        assertEquals(0, matchResults.get(team2Id));
    }

    @Test
    public void testGetMatchesAndWinners_Team2WinsAll() {

        Long team1Id = 1L;
        Long team2Id = 2L;
        Score score1 = new Score();
        score1.setGoals(1);
        score1.setLostGoals(3);
        Score score2 = new Score();
        score2.setGoals(2);
        score2.setLostGoals(4);
        List<Score> scores = Arrays.asList(score1, score2);

        when(scoreRepository.findScoresByTeams(team1Id, team2Id)).thenReturn(scores);


        Map<Long, Integer> matchResults = scoreService.getMatchesAndWinners(team1Id, team2Id);
        assertEquals(0, matchResults.get(team1Id));
        assertEquals(2, matchResults.get(team2Id));
    }

    @Test
    public void testGetMatchesAndWinners_AllMatchesDraw() {

        Long team1Id = 1L;
        Long team2Id = 2L;
        Score score1 = new Score();
        score1.setGoals(2);
        score1.setLostGoals(2);
        Score score2 = new Score();
        score2.setGoals(3);
        score2.setLostGoals(3);
        List<Score> scores = Arrays.asList(score1, score2);

        when(scoreRepository.findScoresByTeams(team1Id, team2Id)).thenReturn(scores);

        Map<Long, Integer> matchResults = scoreService.getMatchesAndWinners(team1Id, team2Id);
        assertEquals(0, matchResults.get(team1Id));
        assertEquals(0, matchResults.get(team2Id));
    }
}