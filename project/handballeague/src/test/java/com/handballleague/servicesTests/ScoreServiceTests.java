package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Match;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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


    @Test
    public void testCreateScore_WithValidInput_ReturnsScore() {
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        Match match = new Match();
        Team team = new Team("Team 1");
        score.setMatch(match);
        score.setTeam(team);

        scoreService.create(score);

        verify(scoreRepository).save(score);
    }

    @Test
    public void testUpdateScore_WithValidInput_UpdatesScore() {
        Long id = 1L;
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        Match match = new Match();
        Team team = new Team("Team 1");
        score.setMatch(match);
        score.setTeam(team);

        when(scoreRepository.findById(id)).thenReturn(Optional.of(score));

        scoreService.update(id, score);

        verify(scoreRepository).save(score);
    }

    @Test
    public void testDeleteScore_WithValidInput_DeletesScore() {
        Long id = 1L;
        when(scoreRepository.existsById(id)).thenReturn(true);

        scoreService.delete(id);

        verify(scoreRepository).deleteById(id);
    }


    @Test
    public void testGetById_WithValidInput_ReturnsScore() {

        Long id = 1L;
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);
        when(scoreRepository.findById(id)).thenReturn(Optional.of(score));

        Score result = scoreService.getById(id);

        assertEquals(score, result);
    }

    @Test
    public void testGetAll_WithValidInput_ReturnsAllScores() {
        Score score1 = new Score();
        score1.setGoals(3);
        score1.setLostGoals(1);
        Score score2 = new Score();
        score2.setGoals(4);
        score2.setLostGoals(2);
        List<Score> scores = Arrays.asList(score1, score2);
        when(scoreRepository.findAll()).thenReturn(scores);

        List<Score> result = scoreService.getAll();

        assertEquals(scores, result);
    }


    @Test
    public void testCheckIfEntityExistsInDb_WithExistingEntity_ReturnsTrue() {
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        Match match = new Match();
        Team team = new Team("Team 1");
        score.setMatch(match);
        score.setTeam(team);

        when(scoreRepository.findAll()).thenReturn(List.of(score));

        boolean result = scoreService.checkIfEntityExistsInDb(score);

        assertTrue(result);
    }

    @Test
    public void testCheckIfEntityExistsInDb_WithExistingId_ReturnsTrue() {
        Long id = 1L;
        Score score = new Score();
        score.setUuid(id);
        score.setGoals(3);
        score.setLostGoals(1);
        when(scoreRepository.findAll()).thenReturn(List.of(score));

        boolean result = scoreService.checkIfEntityExistsInDb(id);

        assertTrue(result);
    }

    @Test
    public void testCheckIfEntityExistsInDb_WithNonExistingId_ReturnsFalse() {
        Long id = 2L;
        Score score = new Score();
        score.setUuid(1L);
        score.setGoals(3);
        score.setLostGoals(1);
        when(scoreRepository.findAll()).thenReturn(List.of(score));

        boolean result = scoreService.checkIfEntityExistsInDb(id);

        assertFalse(result);
    }

    @Test
    public void testGetById_WithInvalidId_ThrowsInvalidArgumentException() {
        Long id = -1L;

        assertThrows(InvalidArgumentException.class, () -> scoreService.getById(id));
    }

    @Test
    public void testGetById_WithNonExistingId_ThrowsObjectNotFoundInDataBaseException() {
        Long id = 1L;
        when(scoreRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundInDataBaseException.class, () -> scoreService.getById(id));
    }


    @Test
    public void testCreateScore_WithNullInput_ThrowsException() {
        assertThrows(InvalidArgumentException.class, () -> scoreService.create(null));
    }

    @Test
    public void testUpdateScore_WithNullInput_ThrowsException() {
        Long id = 1L;

        assertThrows(InvalidArgumentException.class, () -> scoreService.update(id, null));
    }

    @Test
    public void testDeleteScore_WithInvalidId_ThrowsException() {
        Long id = -1L;

        assertThrows(InvalidArgumentException.class, () -> scoreService.delete(id));
    }

    @Test
    public void testUpdateScore_WithNonExistingId_ThrowsException() {

        Long id = 1L;
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        Match match = new Match();
        Team team = new Team("Team 1");
        score.setMatch(match);
        score.setTeam(team);

        when(scoreRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundInDataBaseException.class, () -> scoreService.update(id, score));
    }

    @Test
    public void testDeleteScore_WithNonExistingId_ThrowsException() {
        Long id = 1L;
        when(scoreRepository.existsById(id)).thenReturn(false);

        assertThrows(ObjectNotFoundInDataBaseException.class, () -> scoreService.delete(id));
    }

    @Test
    public void testCreateScore_WithExistingScore_ThrowsEntityAlreadyExistsException() {
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        Match match = new Match();
        Team team = new Team("Team 1");
        score.setMatch(match);
        score.setTeam(team);

        when(scoreRepository.findAll()).thenReturn(List.of(score));

        assertThrows(EntityAlreadyExistsException.class, () -> scoreService.create(score));
    }

    @Test
    public void testCreateScore_WithInvalidScore_ThrowsInvalidArgumentException() {
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        assertThrows(InvalidArgumentException.class, () -> scoreService.create(score));
    }


    @Test
    public void testUpdateScore_WithExistingScore_ThrowsEntityAlreadyExistsException() {
        Long id = 1L;
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        Match match = new Match();
        Team team = new Team("Team 1");
        score.setMatch(match);
        score.setTeam(team);

        when(scoreRepository.findById(id)).thenReturn(Optional.of(score));
        when(scoreRepository.findAll()).thenReturn(List.of(score));

        assertThrows(EntityAlreadyExistsException.class, () -> scoreService.update(id, score));
    }

    @Test
    public void testUpdateScore_WithInvalidScore_ThrowsInvalidArgumentException() {
        Long id = 1L;
        Score score = new Score();
        score.setGoals(3);
        score.setLostGoals(1);

        assertThrows(InvalidArgumentException.class, () -> scoreService.update(id, score));
    }


}