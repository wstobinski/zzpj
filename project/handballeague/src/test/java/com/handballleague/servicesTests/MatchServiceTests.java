package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.*;
import com.handballleague.repositories.*;
import com.handballleague.services.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamContestRepository teamContestRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMatch() throws InvalidArgumentException, EntityAlreadyExistsException {
        Match match = new Match();
        match.setHomeTeam(new Team());
        match.setAwayTeam(new Team());
        match.setGameDate(LocalDateTime.now());

        when(matchRepository.save(any(Match.class))).thenReturn(match);

        Match createdMatch = matchService.create(match);

        assertNotNull(createdMatch);
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    void testCreateMatchWithInvalidArgument() {
        assertThrows(InvalidArgumentException.class, () -> matchService.create(null));
    }

    @Test
    void testCreateMatchThatAlreadyExists() {
        Match match = new Match();
        match.setHomeTeam(new Team());
        match.setAwayTeam(new Team());
        match.setGameDate(LocalDateTime.now());

        when(matchRepository.findAll()).thenReturn(List.of(match));

        assertThrows(EntityAlreadyExistsException.class, () -> matchService.create(match));
    }

    @Test
    void testDeleteMatch() throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        Long matchId = 1L;
        when(matchRepository.existsById(matchId)).thenReturn(true);

        boolean result = matchService.delete(matchId);

        assertTrue(result);
        verify(matchRepository, times(1)).deleteById(matchId);
    }

    @Test
    void testDeleteMatchWithInvalidId() {
        assertThrows(InvalidArgumentException.class, () -> matchService.delete(-1L));
    }

    @Test
    void testDeleteMatchThatDoesNotExist() {
        Long matchId = 1L;
        when(matchRepository.existsById(matchId)).thenReturn(false);

        assertThrows(ObjectNotFoundInDataBaseException.class, () -> matchService.delete(matchId));
    }


    @Test
    void testUpdateMatchWithInvalidId() {
        assertThrows(InvalidArgumentException.class, () -> matchService.update(-1L, new Match()));
    }

    @Test
    void testUpdateMatchThatDoesNotExist() {
        Long matchId = 1L;
        Match updatedMatch = new Match();
        updatedMatch.setHomeTeam(new Team());
        updatedMatch.setAwayTeam(new Team());
        updatedMatch.setGameDate(LocalDateTime.now());

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundInDataBaseException.class, () -> matchService.update(matchId, updatedMatch));
    }

    @Test
    void testGetById() throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        Long matchId = 1L;
        Match match = new Match();

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        Match retrievedMatch = matchService.getById(matchId);

        assertNotNull(retrievedMatch);
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    void testGetByIdWithInvalidId() {
        assertThrows(InvalidArgumentException.class, () -> matchService.getById(-1L));
    }

    @Test
    void testGetByIdThatDoesNotExist() {
        Long matchId = 1L;

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundInDataBaseException.class, () -> matchService.getById(matchId));
    }



    @Test
    void testCheckIfEntityExistsInDbByEntity() {
        Match match = new Match();
        when(matchRepository.findAll()).thenReturn(List.of(match));

        boolean exists = matchService.checkIfEntityExistsInDb(match);

        assertTrue(exists);
    }

    @Test
    void testGetAllMatches() {
        when(matchRepository.findAll()).thenReturn(List.of(new Match()));

        List<Match> matches = matchService.getAll();

        assertNotNull(matches);
        assertFalse(matches.isEmpty());
    }

    @Test
    void testGetFinishedMatches() {
        Match finishedMatch = new Match();
        finishedMatch.setFinished(true);

        when(matchRepository.findAll()).thenReturn(List.of(finishedMatch, new Match()));

        List<Match> finishedMatches = matchService.getFinishedMatches();

        assertNotNull(finishedMatches);
        assertEquals(1, finishedMatches.size());
        assertTrue(finishedMatches.get(0).isFinished());
    }






}
