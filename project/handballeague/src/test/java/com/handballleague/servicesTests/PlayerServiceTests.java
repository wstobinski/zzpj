package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Player;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamRepository;
import com.handballleague.services.PlayerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTests {
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private TeamRepository teamRepository;

    private AutoCloseable autoCloseable;
    private PlayerService underTestService;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new PlayerService(playerRepository, teamRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    /** CREATE METHOD TESTS **/

    @Test
    void createPlayer_WithValidInput_ReturnsPlayer() {
        //given
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);

        //when
        underTestService.create(p);

        //then
        ArgumentCaptor<Player> argumentCaptor = ArgumentCaptor.forClass(Player.class);

        verify(playerRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(p);
    }

    @Test
    void createPlayer_WithNullInput_ThrowsException() {
        //given
        Player p = null;

        //when
        assertThatThrownBy(
                () -> underTestService.create(p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed parameter is invalid");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createPlayer_ThatAlreadyExists_ThrowsException() {
        //given
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);
        List<Player> players = new ArrayList<>();
        players.add(p);
        underTestService.create(p);
        given(playerRepository.findAll()).willReturn(players);

        //when
        assertThatThrownBy(
                () -> underTestService.create(p))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Player with given data already exists in the database");

        //then
        verify(playerRepository, times(1)).save(any());
    }

    @Test
    void createPlayer_WithInvalidFirstName_ThrowsException(){
        //given
        Player p = new Player("",
                "Smith",
                "888999777",
                16,
                true,
                false);

        //when
        assertThatThrownBy(
                () -> underTestService.create(p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of players parameters is invalid.");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createPlayer_WithInvalidLastName_ThrowsException(){
        //given
        Player p = new Player("John",
                "",
                "888999777",
                16,
                true,
                false);

        //when
        assertThatThrownBy(
                () -> underTestService.create(p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of players parameters is invalid.");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createPlayer_WithEmptyPhoneNumber_DoesNotThrow(){
        //given
        Player p = new Player("John",
                "Smith",
                "",
                16,
                true,
                false);

        //when
        assertThatCode(() -> underTestService.create(p))
                .doesNotThrowAnyException();
        //then
        verify(playerRepository).save(any());
    }

    @Test
    void createPlayer_WithInvalidPitchNumber_ThrowsException(){
        //given
        Player p = new Player("John",
                "Smith",
                "888999777",
                -16,
                true,
                false);

        //when
        assertThatThrownBy(
                () -> underTestService.create(p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of players parameters is invalid.");

        //then
        verify(playerRepository, never()).save(any());
    }

    /** DELETE METHOD TESTS **/

    @Test
    void deletePlayerWithValidInput_ReturnsTrue() {
        // given
        long id = 10;
        given(playerRepository.existsById(String.valueOf(id))).willReturn(true);

        // when
        underTestService.delete(id);

        // then
        verify(playerRepository).deleteById(String.valueOf(id));
    }

    @Test
    void deletePlayerWithIdLessThanZero_ThrowingExc(){
        //given
        long id = -10;

        //when
        assertThatThrownBy(() -> underTestService.delete(id))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        //then
        verify(playerRepository, never()).deleteById(any());
    }

    @Test
    void deletePlayerWhenPlayerNotFound_ThrowingExc(){
        //given
        long id = 10;
        given(playerRepository.existsById(String.valueOf(id))).willReturn(false);

        //when
        assertThatThrownBy(() -> underTestService.delete(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Player with id: 10 not found in the database.");

        //then
        verify(playerRepository, never()).deleteById(any());
    }

    /** UPDATE METHOD TESTS **/

    @Test
    void updatePlayerWithValidInput_ReturnsPlayer() {
        //given
        Player p = new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false);
        long id = 10;
        given(playerRepository.findById(String.valueOf(id))).willReturn(Optional.of(p));
        Player p2 = new Player("Adam",
                "Johnson",
                "888999777",
                16,
                true,
                false);

        //when
        underTestService.update(id, p2);

        //then
        assertThat(p.getUuid()).isEqualTo(p2.getUuid());
        assertThat(p.getFirstName()).isEqualTo(p2.getFirstName());
        assertThat(p.getLastName()).isEqualTo(p2.getLastName());
        assertThat(p.getPhoneNumber()).isEqualTo(p2.getPhoneNumber());
        assertThat(p.getPitchNumber()).isEqualTo(p2.getPitchNumber());
        assertThat(p.isCaptain()).isEqualTo(p2.isCaptain());
        assertThat(p.isSuspended()).isEqualTo(p2.isSuspended());

        verify(playerRepository).save(p);
    }

    @Test
    void updatePlayerWithIdLessThanZero_ThrowsExc() {
        //given
        long id = -10;
        Player p = new Player("Adam",
                "Johnson",
                "888999777",
                16,
                true,
                false);

        //when
        assertThatThrownBy(() -> underTestService.update(id, p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updatePlayerWithNullPlayerParameter_ThrowsExc() {
        //given
        long id = 10;
        Player p = null;

        //when
        assertThatThrownBy(() -> underTestService.update(id, p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New player is null.");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updatePlayerThatDoesNotExist_ThrowsExc() {
        //given
        long id = 10;
        Player p = new Player("Adam",
                "Johnson",
                "888999777",
                16,
                true,
                false);
        assertThat(playerRepository.existsById(String.valueOf(id))).isFalse();

        //when
        assertThatThrownBy(() -> underTestService.update(id, p))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Player with given id was not found in the database.");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updatePlayerWithInvalidFirstName_ThrowsExc(){
        //given
        long id = 10;
        Player p = new Player("",
                "Johnson",
                "888999777",
                16,
                true,
                false);

        //when
        assertThatThrownBy(
                () -> underTestService.update(id, p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of players parameters is invalid.");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updatePlayerWithInvalidLastName_ThrowsExc(){
        //given
        long id = 10;
        Player p = new Player("Adam",
                "",
                "888999777",
                16,
                true,
                false);

        //when
        assertThatThrownBy(
                () -> underTestService.update(id, p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of players parameters is invalid.");

        //then
        verify(playerRepository, never()).save(any());
    }
    @Test
    void updatePlayerWithEmptyPhoneNumber_DoesNotThrow(){
        //given
        long id = 10;
        Player existingPlayer = new Player("Adam", "Johnson", "1234567890", 16, true, false);
        Player updatedPlayer = new Player("Adam", "Johnson", "", 16, true, false);

        // Mock the player repository to return the existing player when findById is called
        when(playerRepository.findById(String.valueOf(id))).thenReturn(Optional.of(existingPlayer));

        //when & then
        assertThatCode(() -> underTestService.update(id, updatedPlayer))
                .doesNotThrowAnyException();

        // Verify that the save method was called with the updated player
        verify(playerRepository).save(updatedPlayer);
    }

    @Test
    void updatePlayerWithInvalidPitchNumber_ThrowsExc(){
        //given
        long id = 10;
        Player p = new Player("Adam",
                "Johnson",
                "888999777",
                -16,
                true,
                false);

        //when
        assertThatThrownBy(
                () -> underTestService.update(id, p))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of players parameters is invalid.");

        //then
        verify(playerRepository, never()).save(any());
    }

    /** getById */

    @Test
    void getByIdWithValidInput() {
        //given
        Player p = new Player("John",
                "Smith",
                "123456789",
                16,
                true,
                false);
        long id = 10;

        given(playerRepository.findById(String.valueOf(id))).willReturn(Optional.of(p));

        //when
        underTestService.getById(id);

        //then
        verify(playerRepository).findById(String.valueOf(id));
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
        verify(playerRepository, never()).findById(any());
    }

    @Test
    void getById_WhenPlayerNotFound_ThrowsObjectNotFoundInDataBaseException() {
        //given
        long id = 10;
        given(playerRepository.findById(String.valueOf(id))).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTestService.getById(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Player with given id was not found in the database.");
    }

    /** checkIfEntityExistsInDb */

    @Test
    void checkIfEntityExistsInDb() {
        //given
        Player p = new Player("John",
                "Smith",
                "123456789",
                16,
                true,
                false);
        List<Player> players = new ArrayList<>();
        players.add(p);
        underTestService.create(p);
        given(playerRepository.findAll()).willReturn(players);

        //when
        boolean exists = underTestService.checkIfEntityExistsInDb(p);

        //then
        assertThat(exists).isTrue();
    }

    @Test
    void checkIfEntityDoesNotExistsInDb() {
        //given
        Player p = new Player("John",
                "Smith",
                "123456789",
                16,
                true,
                false);
        List<Player> players = new ArrayList<>();
        given(playerRepository.findAll()).willReturn(players);

        //when
        boolean exists = underTestService.checkIfEntityExistsInDb(p);

        //then
        assertThat(exists).isFalse();
    }

    @Test
    void getAll_ReturnsListOfPlayers() {
        // Given
        List<Player> players = Arrays.asList(
                new Player("John",
                        "Smith",
                        "123456789",
                        16,
                        true,
                        false),
                new Player("Adam",
                        "Johns",
                        "987654321",
                        12,
                        false,
                        false)
        );
        Sort sortByPlayerId = Sort.by(Sort.Direction.ASC, "uuid");
        when(playerRepository.findAll(sortByPlayerId)).thenReturn(players);

        // When
        List<Player> retrievedPlayers = underTestService.getAll();

        // Then
        assertThat(retrievedPlayers).isNotNull();
        assertThat(retrievedPlayers.size()).isEqualTo(players.size());
    }
}
