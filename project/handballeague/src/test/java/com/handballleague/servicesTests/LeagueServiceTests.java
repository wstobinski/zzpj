package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Team;
import com.handballleague.repositories.*;
import com.handballleague.services.LeagueService;
import com.handballleague.services.TeamContestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeagueServiceTests {
    @Mock
    private LeagueRepository leagueRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private RoundRepository roundRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private RefereeRepository refereeRepository;
    @Mock
    private TeamContestService teamContestService;
    @Mock
    private TeamContestRepository teamContestRepository;
    @Mock
    private ScoreRepository scoreRepository;

    private AutoCloseable autoCloseable;
    private LeagueService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new LeagueService(leagueRepository, teamRepository, roundRepository, matchRepository, refereeRepository, teamContestService, teamContestRepository, scoreRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    void createLeague_WithValidInput_ReturnsLeague() {
        League league = new League();
        league.setName("Premier League");
        league.setStartDate(LocalDateTime.now());

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        List<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        league.setTeams(teams);

        underTestService.create(league);

        ArgumentCaptor<League> argumentCaptor = ArgumentCaptor.forClass(League.class);

        verify(leagueRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(league);
    }

    @Test
    void createLeague_WithNullInput_ThrowsException() {
        assertThatThrownBy(
                () -> underTestService.create(null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed parameter is invalid");

        verify(leagueRepository, never()).save(any());
    }

    @Test
    void createLeague_ThatAlreadyExists_ThrowsException() {
        League league = new League();
        league.setName("Premier League");
        league.setStartDate(LocalDateTime.now());

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        Team team3 = new Team("Team 3");

        List<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        league.setTeams(teams);

        List<League> leagues = new ArrayList<>();
        leagues.add(league);
        underTestService.create(league);
        given(leagueRepository.findAll()).willReturn(leagues);

        assertThatThrownBy(
                () -> underTestService.create(league))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("League with given data already exists in the database");

        verify(leagueRepository, times(1)).save(any());
    }

    /** DELETE METHOD TESTS **/


    @Test
    void deleteLeagueWithIdLessThanZero_ThrowsException() {
        long id = -10;

        assertThatThrownBy(() -> underTestService.delete(id))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        verify(leagueRepository, never()).deleteById(any());
    }

    @Test
    void deleteLeagueWithValidInput_ReturnsTrue() {
        long id = 10;
        League league = new League();
        league.setRounds(new ArrayList<>()); // Initialize the rounds field
        given(leagueRepository.findById(id)).willReturn(Optional.of(league));

        underTestService.delete(id);

        ArgumentCaptor<League> argumentCaptor = ArgumentCaptor.forClass(League.class);
        verify(leagueRepository).delete(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(league);
    }

    @Test
    void deleteLeagueWhenLeagueNotFound_ThrowsException() {
        long id = 10;

        assertThatThrownBy(() -> underTestService.delete(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("League not found");

        verify(leagueRepository, never()).delete(any());
    }

    /** UPDATE METHOD TESTS **/

    @Test
    void updateLeagueWithValidInput_ReturnsLeague() {
        League league = new League();
        league.setName("Premier League");
        league.setStartDate(LocalDateTime.now());
        long id = 10;
        given(leagueRepository.findById(id)).willReturn(Optional.of(league));
        League newLeague = new League();
        newLeague.setName("La Liga");
        newLeague.setStartDate(LocalDateTime.now());

        underTestService.update(id, newLeague);

        assertThat(league.getName()).isEqualTo(newLeague.getName());
        assertThat(league.getStartDate()).isEqualTo(newLeague.getStartDate());

        verify(leagueRepository).save(league);
    }

    @Test
    void updateLeagueWithIdLessThanZero_ThrowsExc() {
        long id = -10;
        League league = new League();
        league.setName("Premier League");
        league.setStartDate(LocalDateTime.now());

        assertThatThrownBy(() -> underTestService.update(id, league))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        verify(leagueRepository, never()).save(any());
    }

    @Test
    void updateLeagueWithNullLeagueParameter_ThrowsExc() {
        long id = 10;

        assertThatThrownBy(() -> underTestService.update(id, null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New league is null.");

        verify(leagueRepository, never()).save(any());
    }

    @Test
    void updateLeagueThatDoesNotExist_ThrowsExc() {
        long id = 10;
        League league = new League();
        league.setName("Premier League");
        league.setStartDate(LocalDateTime.now());

        assertThat(leagueRepository.existsById(id)).isFalse();

        assertThatThrownBy(() -> underTestService.update(id, league))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("League with given id was not found in the database.");

        verify(leagueRepository, never()).save(any());
    }
}