package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamRepository;
import com.handballleague.services.TeamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTests {
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PlayerRepository playerRepository;

    private AutoCloseable autoCloseable;
    private TeamService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new TeamService(teamRepository, playerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    /** CREATE METHOD TESTS **/

    @Test
    void createTeam_WithValidInput_ReturnsTeam() {
        //given
        Team t = new Team("Black Panthers");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        //when
        underTestService.create(t);

        //then
        ArgumentCaptor<Team> argumentCaptor = ArgumentCaptor.forClass(Team.class);

        verify(teamRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(t);
    }

    @Test
    void createTeam_WithNullInput_ThrowsException() {
        //when
        assertThatThrownBy(
                () -> underTestService.create(null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed parameter is invalid");

        //then
        verify(teamRepository, never()).save(any());
    }

    @Test
    void createPlayer_ThatAlreadyExists_ThrowsException() {
        //given
        Team t = new Team("Black Panthers");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        List<Team> teams = new ArrayList<>();
        teams.add(t);
        underTestService.create(t);
        given(teamRepository.findAll()).willReturn(teams);

        //when
        assertThatThrownBy(
                () -> underTestService.create(t))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Team with given data already exists in database");

        //then
        verify(teamRepository, times(1)).save(any());
    }

    @Test
    void createPlayer_WithInvalidName_ThrowsException(){
        //given
        Team t = new Team("");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        //when
        assertThatThrownBy(
                () -> underTestService.create(t))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of team parameters is invalid.");

        //then
        verify(teamRepository, never()).save(any());
    }

    /** DELETE METHOD TESTS **/

    @Test
    void deleteTeamWithValidInput_ReturnsTrue() {
        // given
        long id = 10;
        given(teamRepository.existsById(id)).willReturn(true);

        // when
        underTestService.delete(id);

        // then
        verify(teamRepository).deleteById(id);
    }

    @Test
    void deleteTeamWithIdLessThanZero_ThrowingExc(){
        //given
        long id = -10;

        //when
        assertThatThrownBy(() -> underTestService.delete(id))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        //then
        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    void deleteTeamWhenPatientNotFound_ThrowingExc(){
        //given
        long id = 10;
        given(teamRepository.existsById(id)).willReturn(false);

        //when
        assertThatThrownBy(() -> underTestService.delete(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Team with id: 10 not found in database.");

        //then
        verify(teamRepository, never()).deleteById(any());
    }

    /** UPDATE METHOD TESTS **/

    @Test
    void updateTeamWithValidInput_ReturnsTeam() {
        //given
        Team t = new Team("Sky Rockets");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);
        long id = 10;
        given(teamRepository.findById(id)).willReturn(Optional.of(t));
        Team t2 = new Team("Wild Buffalo");
        Player p2 = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players2 = new ArrayList<>();
        players2.add(p2);

        t2.setPlayers(players);

        //when
        underTestService.update(id, t2);

        //then
        assertThat(t.getUuid()).isEqualTo(t2.getUuid());
        assertThat(t.getTeamName()).isEqualTo(t2.getTeamName());
        assertThat(t.getPlayers()).isEqualTo(t2.getPlayers());

        verify(teamRepository).save(t);
    }

    @Test
    void updateTeamWithIdLessThanZero_ThrowsExc() {
        //given
        long id = -10;
        Team t = new Team("Sky Rockets");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        //when
        assertThatThrownBy(() -> underTestService.update(id, t))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        //then
        verify(teamRepository, never()).save(any());
    }

    @Test
    void updateTeamWithNullTeamParameter_ThrowsExc() {
        //given
        long id = 10;

        //when
        assertThatThrownBy(() -> underTestService.update(id, null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New team is null.");

        //then
        verify(teamRepository, never()).save(any());
    }

    @Test
    void updateTeamThatDoesNotExist_ThrowsExc() {
        //given
        long id = 10;
        Team t = new Team("Sky Rockets");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        assertThat(playerRepository.existsById(String.valueOf(id))).isFalse();

        //when
        assertThatThrownBy(() -> underTestService.update(id, t))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Team with given id was not found in database.");

        //then
        verify(teamRepository, never()).save(any());
    }

    @Test
    void updateTeamWithInvalidName_ThrowsExc(){
        //given
        long id = 10;
        Team t = new Team("");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        //when
        assertThatThrownBy(
                () -> underTestService.update(id, t))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed invalid arguments (team name).");

        //then
        verify(teamRepository, never()).save(any());
    }

    /** getById */

    @Test
    void getByIdWithValidInput() {
        //given
        long id = 10;
        Team t = new Team("Fast Panthers");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        given(teamRepository.findById(id)).willReturn(Optional.of(t));

        //when
        underTestService.getById(id);

        //then
        verify(teamRepository).findById(id);
    }

    @Test
    void getByIdWithInvalidIdParameter() {
        //given
        long id = -10;

        //when
        assertThatThrownBy(() -> underTestService.getById(id))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        //then
        verify(teamRepository, never()).findById(any());
    }

    @Test
    void getById_WhenTeamNotFound_ThrowsObjectNotFoundInDataBaseException() {
        //given
        long id = 10;
        given(teamRepository.findById(id)).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTestService.getById(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Object with given id was not found in database.");
    }

    /** checkIfEntityExistsInDb */

    @Test
    void checkIfEntityExistsInDb() {
        //given
        Team t = new Team("Fast Panthers");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        List<Team> teams = new ArrayList<>();
        teams.add(t);
        underTestService.create(t);
        given(teamRepository.findAll()).willReturn(teams);

        //when
        boolean exists = underTestService.checkIfEntityExistsInDb(t);

        //then
        assertThat(exists).isTrue();
    }

    @Test
    void checkIfEntityDoesNotExistsInDb() {
        //given
        long id = 10;
        Team t = new Team("Fast Panthers");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        List<Team> teams = new ArrayList<>();
        given(teamRepository.findAll()).willReturn(teams);

        //when
        boolean exists = underTestService.checkIfEntityExistsInDb(t);

        //then
        assertThat(exists).isFalse();
    }

    @Test
    void getAll_ReturnsListOfPlayers() {
        Team t1 = new Team("Fast Panthers");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t1.setPlayers(players);


        Team t2 = new Team("Slow Snails");
        Player p2 = new Player("Jude",
                "Parker",
                "987123456",
                2,
                false,
                false);

        List<Player> players2 = new ArrayList<>();
        players2.add(p2);

        // Given
        List<Team> teams = Arrays.asList(t1, t2);

        when(teamRepository.findAll()).thenReturn(teams);

        // When
        List<Team> retrievedTeams = underTestService.getAll();

        // Then
        assertThat(retrievedTeams).isNotNull();
        assertThat(retrievedTeams.size()).isEqualTo(teams.size());
    }

    @Test
    void addPlayerToTeam_WithValidInput_ReturnsTeamWithPlayer() {
        // Arrange
        Long teamId = 1L;
        Long playerId = 1L;
        Team t = new Team("Fast Panthers");
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        List<Player> players = new ArrayList<>();
        players.add(p);

        t.setPlayers(players);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(t));
        when(playerRepository.findById(String.valueOf(playerId))).thenReturn(Optional.of(p));
        when(teamRepository.save(t)).thenReturn(t);

        // Act
        Team result = underTestService.addPlayerToTeam(teamId, playerId);

        // Assert
        assertThat(result).isNotNull();
        boolean playerFound = false;
        for (Player teamPlayer : result.getPlayers()) {
            if (teamPlayer.equals(p)) {
                playerFound = true;
                break;
            }
        }
        assertThat(playerFound).isTrue();
        verify(teamRepository, times(1)).save(t);
    }

    @Test
    void addPlayerToTeam_WithInvalidTeamId_ThrowsRuntimeException() {
        // Arrange
        Long teamId = 1L;
        Long playerId = 1L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> underTestService.addPlayerToTeam(teamId, playerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Team not found");
        verify(teamRepository, never()).save(any());
    }

    @Test
    void addPlayerToTeam_WithInvalidPlayerId_ThrowsRuntimeException() {
        // Arrange
        Long teamId = 1L;
        Long playerId = 1L;
        Team team = new Team("Black Panthers");
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.findById(String.valueOf(playerId))).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> underTestService.addPlayerToTeam(teamId, playerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Player not found");
        verify(teamRepository, never()).save(any());
    }
}
