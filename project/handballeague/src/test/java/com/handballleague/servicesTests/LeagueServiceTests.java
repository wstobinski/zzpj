package com.handballleague.servicesTests;

import com.handballleague.DTO.GenerateScheduleDTO;
import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.*;
import com.handballleague.repositories.*;
import com.handballleague.services.LeagueService;
import com.handballleague.services.TeamContestService;
import com.handballleague.util.DateManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void createLeagueWithLessThanThreeTeams_ThrowsException() {
        League league = new League();
        league.setName("Premier League");
        league.setStartDate(LocalDateTime.now());

        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");

        List<Team> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        league.setTeams(teams);

        assertThatThrownBy(() -> underTestService.create(league))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("League needs to have at least 3 teams, and no more than 12 teams.");

        verify(leagueRepository, never()).save(any());
    }

    @Test
    void createLeagueWithMoreThanTwelveTeams_ThrowsException() {
        League league = new League();
        league.setName("Premier League");
        league.setStartDate(LocalDateTime.now());

        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            teams.add(new Team("Team " + (i + 1)));
        }

        league.setTeams(teams);

        assertThatThrownBy(() -> underTestService.create(league))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("League needs to have at least 3 teams, and no more than 12 teams.");

        verify(leagueRepository, never()).save(any());
    }


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

    @Test
    void deleteScoresAndMatch_WithValidInput_DeletesScoresAndMatch() {
        Match match = new Match();
        Score score1 = new Score();
        Score score2 = new Score();
        List<Score> scores = Arrays.asList(score1, score2);
        given(scoreRepository.findByMatch(match)).willReturn(Optional.of(scores));

        Round round = new Round();
        round.setUuid(1L);
        List<Match> matches = Arrays.asList(match);
        round.setMatches(matches);

        League league = new League();
        league.setUuid(1L);
        league.setRounds(Arrays.asList(round));
        given(leagueRepository.findById(1L)).willReturn(Optional.of(league));

        underTestService.delete(league.getUuid());

        verify(scoreRepository, times(1)).deleteAll(scores);
        verify(matchRepository, times(1)).delete(match);
    }


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

    @Test
    void updateLeagueWithInvalidNewLeague_ThrowsExc() {
        long id = 10;
        League league = new League();
        league.setName(""); // Empty name
        league.setStartDate(null); // Null start date

        assertThatThrownBy(() -> underTestService.update(id, league))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed invalid arguments.");

        verify(leagueRepository, never()).save(any());
    }


    @Test
    void getByIdWithIdLessThanOrEqualToZero_ThrowsException() {
        long id = -10;

        assertThatThrownBy(() -> underTestService.getById(id))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        verify(leagueRepository, never()).findById(any());
    }

    @Test
    void getByIdWhenLeagueNotFound_ThrowsException() {
        long id = 10;
        given(leagueRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.getById(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("League with given id was not found in the database.");

        verify(leagueRepository).findById(id);
    }


    @Test
    void getAll_ReturnsAllLeagues() {
        League league1 = new League();
        league1.setName("Premier League");
        league1.setStartDate(LocalDateTime.now());

        League league2 = new League();
        league2.setName("La Liga");
        league2.setStartDate(LocalDateTime.now());

        List<League> leagues = new ArrayList<>();
        leagues.add(league1);
        leagues.add(league2);

        given(leagueRepository.findAll()).willReturn(leagues);

        List<League> returnedLeagues = underTestService.getAll();

        assertThat(returnedLeagues).isEqualTo(leagues);
    }

    @Test
    void finishLeague_WithValidInput_FinishesLeague() {
        Long leagueId = 1L;
        League league = new League();
        league.setUuid(leagueId);
        league.setTeams(new ArrayList<>());
        given(leagueRepository.findById(leagueId)).willReturn(Optional.of(league));

        LocalDateTime result = underTestService.finishLeague(leagueId);

        assertThat(result).isNotNull();
        assertThat(league.getFinishedDate()).isNotNull();
        verify(leagueRepository).save(league);
    }

    @Test
    void finishLeague_WithInvalidInput_ThrowsException() {
        Long leagueId = 1L;
        given(leagueRepository.findById(leagueId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.finishLeague(leagueId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("League not found");
    }

    @Test
    void finishLeague_WhenLeagueAlreadyFinished_ThrowsException() {
        Long leagueId = 1L;
        League league = new League();
        league.setUuid(leagueId);
        league.setFinishedDate(LocalDateTime.now());
        given(leagueRepository.findById(leagueId)).willReturn(Optional.of(league));

        assertThatThrownBy(() -> underTestService.finishLeague(leagueId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("League is already finished");
    }


    @Test
    void checkIfEntityExistsInDb_WithExistingLeague_ReturnsTrue() {
        Long leagueId = 1L;
        League league = new League();
        league.setUuid(leagueId);
        given(leagueRepository.findAll()).willReturn(Arrays.asList(league));

        boolean exists = underTestService.checkIfEntityExistsInDb(leagueId);

        assertThat(exists).isTrue();
    }

    @Test
    void checkIfEntityExistsInDb_WithNonExistingLeague_ReturnsFalse() {
        Long leagueId = 1L;
        given(leagueRepository.findAll()).willReturn(new ArrayList<>());

        boolean exists = underTestService.checkIfEntityExistsInDb(leagueId);

        assertThat(exists).isFalse();
    }


    @Test
    void finishLeague_WithValidInput_SetsLeagueOfEachTeamToNull() {
        Long leagueId = 1L;
        League league = new League();
        league.setUuid(leagueId);
        Team team = new Team("Team 1");
        league.setTeams(List.of(team));
        given(leagueRepository.findById(leagueId)).willReturn(Optional.of(league));

        underTestService.finishLeague(leagueId);

        assertThat(team.getLeague()).isNull();
    }

    @Test
    public void testAddLeagueToTeam() {
        Long leagueId = 1L;
        Long teamId = 1L;

        League league = new League();
        league.setTeams(new ArrayList<>());
        Team team = new Team();

        when(leagueRepository.findById(leagueId)).thenReturn(Optional.of(league));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        underTestService.addLeagueToTeam(leagueId, teamId);

        assertTrue(league.getTeams().contains(team));
        verify(leagueRepository).save(league);
        verify(teamContestService).create(leagueId, teamId);
    }

    @Test
    public void testRemoveTeamFromLeague() {
        Long leagueId = 1L;
        Long teamId = 1L;

        League league = new League();
        league.setTeams(new ArrayList<>());
        Team team = new Team();
        league.getTeams().add(team);

        when(leagueRepository.findById(leagueId)).thenReturn(Optional.of(league));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        underTestService.removeTeamFromLeague(leagueId, teamId);

        assertFalse(league.getTeams().contains(team));
        verify(leagueRepository).save(league);
    }


}