package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Player;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.services.PlayerService;
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
public class PlayerServiceTests {
    @Mock
    private PlayerRepository playerRepository;

    private AutoCloseable autoCloseable;
    private PlayerService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new PlayerService(playerRepository);
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
                .hasMessageContaining("Player with given data already exists in database");

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
    void createPlayer_WithInvalidPhoneNumber_ThrowsException(){
        //given
        Player p = new Player("John",
                "Smith",
                "",
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
    void deletePatientWithIdLessThanZero_ThrowingExc(){
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
    void deletePatientWhenPatientNotFound_ThrowingExc(){
        //given
        long id = 10;
        given(playerRepository.existsById(String.valueOf(id))).willReturn(false);

        //when
        assertThatThrownBy(() -> underTestService.delete(id))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Player with id: 10 not found in database.");

        //then
        verify(playerRepository, never()).deleteById(any());
    }

    /** UPDATE METHOD TESTS **/

    @Test
    void updatePatientWithValidInput_ReturnsPlayer() {
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
    void updatePatientWithIdLessThanZero_ThrowsExc() {
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
    void updatePatientWithNullPlayerParameter_ThrowsExc() {
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
    void updatePatientThatDoesNotExist_ThrowsExc() {
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
                .hasMessageContaining("Player with given id was not found in database.");

        //then
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updatePatientWithInvalidFirstName_ThrowsExc(){
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
    void updatePatientWithInvalidLastName_ThrowsExc(){
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
    void updatePatientWithInvalidPhoneNumber_ThrowsExc(){
        //given
        long id = 10;
        Player p = new Player("Adam",
                "Johnson",
                "",
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
    void updatePatientWithInvalidPitchNumber_ThrowsExc(){
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
                .hasMessageContaining("Object with given id was not found in database.");
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

        when(playerRepository.findAll()).thenReturn(players);

        // When
        List<Player> retrievedPlayers = underTestService.getAll();

        // Then
        assertThat(retrievedPlayers).isNotNull();
        assertThat(retrievedPlayers.size()).isEqualTo(players.size());
    }
}
