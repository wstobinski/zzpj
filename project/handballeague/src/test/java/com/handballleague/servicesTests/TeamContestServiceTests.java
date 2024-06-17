package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Team;
import com.handballleague.model.TeamContest;
import com.handballleague.repositories.TeamContestRepository;
import com.handballleague.services.LeagueService;
import com.handballleague.services.TeamContestService;
import com.handballleague.services.TeamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TeamContestServiceTests {

    @Mock
    private LeagueService leagueService;
    @Mock
    private TeamService teamService;
    @Mock
    private TeamContestRepository teamContestRepository;

    private AutoCloseable autoCloseable;
    private TeamContestService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new TeamContestService(leagueService, teamService, teamContestRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createTeamContest_WithValidInput_ReturnsTeamContest() {
        League league = League.builder().build();
        Team team = Team.builder()
                .teamName("Team1")
                .uuid(1L)
                .build();
        TeamContest teamContest = TeamContest.builder()
                .team(team)
                .league(league).build();

        given(teamService.checkIfEntityExistsInDb(team)).willReturn(true);
        given(leagueService.checkIfEntityExistsInDb(league)).willReturn(true);

        TeamContest created = underTestService.create(teamContest);
        ArgumentCaptor<TeamContest> argumentCaptor = ArgumentCaptor.forClass(TeamContest.class);

        verify(teamContestRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(teamContest);
        assertThat(created).isEqualTo(teamContest);
    }

    @Test
    void createTeamContest_WithNullInput_ThrowsInvalidArgumentException() {
        Team team = Team.builder()
                .teamName("Team1")
                .uuid(1L)
                .build();
        TeamContest teamContest = TeamContest.builder()
                .team(team)
                .league(null).build();

        //when
        assertThatThrownBy(
                () -> underTestService.create(teamContest))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Team contest needs to be connected with both Team and Contest entities");

        //then
        verify(teamContestRepository, never()).save(teamContest);

    }

    @Test
    void createTeamContest_WithDuplicateId_ThrowsEntityAlreadyExistsException() {
        League league = League.builder().build();
        Team team = Team.builder()
                .teamName("Team1")
                .uuid(1L)
                .build();
        TeamContest teamContest = TeamContest.builder()
                .team(team)
                .league(league).build();

        given(teamContestRepository.findAll()).willReturn(List.of(teamContest));

        //when
        assertThatThrownBy(
                () -> underTestService.create(teamContest))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("TeamContest with given data already exists in the database");

        //then
        verify(teamContestRepository, never()).save(teamContest);

    }

    @Test
    void createTeamContest_WithNull_ThrowsInvalidArgumentException() {

        assertThatThrownBy(
                () -> underTestService.create(null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed parameter is invalid");

        //then
        verify(teamContestRepository, never()).save(any());

    }

    @Test
    void createTeamContest_WithInvalidTeam_ThrowsInvalidArgumentException() {
        Team team = Team.builder().build();
        League league = League.builder().build();
        TeamContest teamContest = TeamContest.builder()
                .league(league)
                .team(team)
                .build();
        assertThatThrownBy(
                () -> underTestService.create(teamContest))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Provided Team does not exist in the database");

        //then
        verify(teamContestRepository, never()).save(any());

    }

    @Test
    void createTeamContest_WithInvalidLeague_ThrowsInvalidArgumentException() {
        Team team = Team.builder().build();
        League league = League.builder().build();
        TeamContest teamContest = TeamContest.builder()
                .league(league)
                .team(team)
                .build();
        given(teamService.checkIfEntityExistsInDb(team)).willReturn(true);
        assertThatThrownBy(
                () -> underTestService.create(teamContest))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Provided League does not exist in the database");

        //then
        verify(teamContestRepository, never()).save(any());

    }

    @Test
    void create_WithValidInput_ReturnsTeamContest() throws InvalidArgumentException, EntityAlreadyExistsException {
        long leagueID = 1L;
        long teamID = 1L;

        Team team = Team.builder().uuid(teamID).build();
        League league = League.builder().build();
        TeamContest teamContest = new TeamContest(team, league, 0, 0 , 0, 0, 0, 0);

        given(teamService.getById(teamID)).willReturn(team);
        given(leagueService.getById(leagueID)).willReturn(league);
        given(teamContestRepository.save(teamContest)).willReturn(teamContest);


        // when
        TeamContest result = underTestService.create(leagueID, teamID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTeam()).isEqualTo(team);
        assertThat(result.getLeague()).isEqualTo(league);
        verify(teamContestRepository).save(teamContest);
    }

    @Test
    void create_WithNonExistentTeam_ThrowsInvalidArgumentException() {
        long leagueID = 1L;
        long teamID = 1L;

        given(teamService.getById(teamID)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> underTestService.create(leagueID, teamID))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Provided Team does not exist in the database");
    }

    @Test
    void create_WithNonExistentLeague_ThrowsInvalidArgumentException() {
        long leagueID = 1L;
        long teamID = 1L;

        Team team = Team.builder().uuid(teamID).build();

        given(teamService.getById(teamID)).willReturn(team);
        given(leagueService.getById(leagueID)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> underTestService.create(leagueID, teamID))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Provided League does not exist in the database");
    }

    @Test
    void create_WithExistingTeamContest_ThrowsEntityAlreadyExistsException() {
        long leagueID = 1L;
        long league2Id = 2L;
        long teamID = 1L;

        Team team = Team.builder().uuid(teamID).build();
        League league = League.builder().build();
        League league2 = League.builder().build();
        league.setUuid(leagueID);
        league2.setUuid(league2Id);
        TeamContest teamContest = TeamContest.builder().league(league).team(team).build();
        TeamContest teamContest2 = TeamContest.builder().league(league2).team(team).build();
        given(teamService.getById(teamID)).willReturn(team);
        given(leagueService.getById(leagueID)).willReturn(league);
        given(teamContestRepository.save(teamContest)).willReturn(teamContest);
        underTestService.create(leagueID, teamID);
        given(teamContestRepository.findAll()).willReturn(List.of(teamContest, teamContest2));

        // when & then
        assertThatThrownBy(() -> underTestService.create(leagueID, teamID))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("TeamContest for provided league and team already exists");
    }

    @Test
    void deleteTeamContest_WithValidInput_ReturnsTrue() {
        long teamContestId = 1L;
        given(teamContestRepository.existsById(teamContestId)).willReturn(true);
        // when
        boolean result = underTestService.delete(teamContestId);

        // then
        verify(teamContestRepository, times(1)).deleteById(teamContestId);
        assertThat(result).isTrue();
    }

    @Test
    void deleteTeamContest_WithInvalidId_ThrowsInvalidArgumentException() {
        long teamContestId = 0L;
        assertThatThrownBy(
                () -> underTestService.delete(teamContestId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

    }


    @Test
    void deleteTeamContest_WithNotFoundId_ThrowsObjectNotFoundInDataBaseException() {
        long teamContestId = 10L;
        given(teamContestRepository.existsById(teamContestId)).willReturn(false);
        assertThatThrownBy(
                () -> underTestService.delete(teamContestId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("TeamContest with id: 10 not found in the database.");

    }

    @Test
    void updateTeamContest_WithValidInput_ReturnsUpdatedTeamContest() {
        long teamContestId = 1L;

        League initialLeague = League.builder().build();
        League newLeague = League.builder().build();
        Team team = Team.builder().build();
        TeamContest existingTeamContest = TeamContest.builder().team(team).league(initialLeague).build();
        given(teamContestRepository.findById(teamContestId)).willReturn(Optional.of(existingTeamContest));

        TeamContest updatedTeamContest = TeamContest.builder().team(team).league(newLeague).points(3).build();

        given(teamContestRepository.save(updatedTeamContest)).willReturn(updatedTeamContest);
        // when
        TeamContest result = underTestService.update(teamContestId, updatedTeamContest);

        // then
        assertThat(result.getTeam()).isEqualTo(updatedTeamContest.getTeam());
        assertThat(result.getLeague()).isEqualTo(updatedTeamContest.getLeague());
        assertThat(result.getPoints()).isEqualTo(updatedTeamContest.getPoints());

        verify(teamContestRepository).save(existingTeamContest);
    }

    @Test
    void updateTeamContest_WithInvalidId_ThrowsInvalidArgumentException() {
        long invalidId = 0L;
        TeamContest teamContest = TeamContest.builder().build();

        assertThatThrownBy(
                () -> underTestService.update(invalidId, teamContest))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");
    }

    @Test
    void updateTeamContest_WithNullTeamContest_ThrowsInvalidArgumentException() {
        long teamContestId = 1L;

        assertThatThrownBy(
                () -> underTestService.update(teamContestId, null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New teamContest is null.");
    }

    @Test
    void updateTeamContest_WithMissingTeam_ThrowsInvalidArgumentException() {
        long teamContestId = 1L;
        League league = League.builder().build();
        TeamContest teamContest = TeamContest.builder().league(league).build();

        assertThatThrownBy(
                () -> underTestService.update(teamContestId, teamContest))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed invalid arguments.");
    }

    @Test
    void updateTeamContest_WithMissingLeague_ThrowsInvalidArgumentException() {
        long teamContestId = 1L;
        Team team = Team.builder().build();
        TeamContest teamContest = TeamContest.builder().team(team).build();

        assertThatThrownBy(
                () -> underTestService.update(teamContestId, teamContest))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed invalid arguments.");
    }

    @Test
    void updateTeamContest_WithNonExistentId_ThrowsObjectNotFoundInDataBaseException() {
        long nonExistentId = 99L;
        Team team = Team.builder().build();
        League league = League.builder().build();
        TeamContest teamContest = TeamContest.builder().team(team).league(league).build();

        given(teamContestRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThatThrownBy(
                () -> underTestService.update(nonExistentId, teamContest))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("TeamContest with given id was not found in the database.");
    }


    @Test
    void getById_WithValidId_ReturnsTeamContest() {
        long teamContestId = 1L;
        Team team = Team.builder().build();
        League league = League.builder().build();
        TeamContest teamContest = TeamContest.builder().team(team).league(league).build();

        given(teamContestRepository.findById(teamContestId)).willReturn(Optional.of(teamContest));

        TeamContest result = underTestService.getById(teamContestId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(teamContest);
    }

    @Test
    void getById_WithInvalidId_ThrowsInvalidArgumentException() {
        long invalidId = 0L;

        assertThatThrownBy(
                () -> underTestService.getById(invalidId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");
    }

    @Test
    void getById_WithNonExistentId_ThrowsObjectNotFoundInDataBaseException() {
        long nonExistentId = 99L;

        given(teamContestRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThatThrownBy(
                () -> underTestService.getById(nonExistentId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("TeamContest with given id was not found in the database.");
    }

    @Test
    void getAll_ReturnsListOfTeamContests() {
        Team team = Team.builder().build();
        League league = League.builder().build();
        List<TeamContest> teamContests = Stream.of(
                TeamContest.builder().team(team).league(league).build(),
                TeamContest.builder().team(team).league(league).build()
        ).collect(Collectors.toList());

        given(teamContestRepository.findAllByOrderByPointsDescGoalsAcquiredDescGoalsLostAsc()).willReturn(teamContests);

        List<TeamContest> result = underTestService.getAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(teamContests.size());
        assertThat(result).isEqualTo(teamContests);
    }

    @Test
    void checkIfEntityExistsInDb_WithTeamContest_ReturnsTrue() {
        Team team = Team.builder().build();
        League league = League.builder().build();
        TeamContest teamContest = TeamContest.builder().team(team).league(league).build();
        List<TeamContest> teamContests = List.of(teamContest);

        given(teamContestRepository.findAll()).willReturn(teamContests);

        boolean result = underTestService.checkIfEntityExistsInDb(teamContest);

        assertThat(result).isTrue();
    }

    @Test
    void checkIfEntityExistsInDb_WithTeamContest_ReturnsFalse() {
        Team team = Team.builder().build();
        League league = League.builder().build();
        TeamContest teamContest = TeamContest.builder().team(team).league(league).build();

        given(teamContestRepository.findAll()).willReturn(List.of());

        boolean result = underTestService.checkIfEntityExistsInDb(teamContest);

        assertThat(result).isFalse();
    }

    @Test
    void checkIfEntityExistsInDb_WithEntityID_ThrowsRuntimeException() {
        long entityId = 1L;

        assertThatThrownBy(
                () -> underTestService.checkIfEntityExistsInDb(entityId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not applicable");
    }

    @Test
    void checkIfEntityExistsInDb_WithTeamAndLeagueId_ReturnsTrue() {
        long teamId = 1L;
        long leagueId = 1L;
        Team team = Team.builder().uuid(teamId).build();
        League league = League.builder().build();
        league.setUuid(leagueId);
        TeamContest teamContest = TeamContest.builder().team(team).league(league).build();
        List<TeamContest> teamContests = List.of(teamContest);

        given(teamContestRepository.findAll()).willReturn(teamContests);

        boolean result = underTestService.checkIfEntityExistsInDb(teamId, leagueId);

        assertThat(result).isTrue();
    }

    @Test
    void checkIfEntityExistsInDb_WithTeamAndLeagueId_ReturnsFalse() {
        long teamId = 1L;
        long leagueId = 1L;

        given(teamContestRepository.findAll()).willReturn(List.of());

        boolean result = underTestService.checkIfEntityExistsInDb(teamId, leagueId);

        assertThat(result).isFalse();
    }

    @Test
    void findTeamContestsInCertainLeague_ReturnsTeamContests() {
        long leagueId = 1L;
        League league = League.builder().build();
        league.setUuid(leagueId);
        Team team1 = Team.builder().build();
        Team team2 = Team.builder().build();
        List<TeamContest> teamContests = Stream.of(
                TeamContest.builder().team(team1).league(league).build(),
                TeamContest.builder().team(team2).league(league).build()
        ).collect(Collectors.toList());

        given(teamContestRepository.findAllByOrderByPointsDescGoalsAcquiredDescGoalsLostAsc()).willReturn(teamContests);

        List<TeamContest> result = underTestService.findTeamContestsInCertainLeague(leagueId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(teamContests.size());
        assertThat(result).isEqualTo(teamContests);
    }

    @Test
    void findTeamContestsInCertainLeague_ReturnsEmptyList() {
        long leagueId = 1L;

        given(teamContestRepository.findAllByOrderByPointsDescGoalsAcquiredDescGoalsLostAsc()).willReturn(List.of());

        List<TeamContest> result = underTestService.findTeamContestsInCertainLeague(leagueId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
    }
}