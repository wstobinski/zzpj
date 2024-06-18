package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Round;
import com.handballleague.repositories.LeagueRepository;
import com.handballleague.repositories.MatchRepository;
import com.handballleague.repositories.RefereeRepository;
import com.handballleague.repositories.RoundRepository;
import com.handballleague.services.RefereeService;
import com.handballleague.services.RoundService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class RoundServiceTest {

    @Mock
    private RoundRepository roundRepository;
    @Mock
    private LeagueRepository leagueRepository;

    private AutoCloseable autoCloseable;
    private RoundService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new RoundService(roundRepository, leagueRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createRound_WithValidInput_ReturnsRound() {
        // given
        League contest = new League("Premier League", LocalDateTime.now());
        Round round = new Round(1, LocalDateTime.now(), contest);
        given(roundRepository.save(round)).willReturn(round);
        given(roundRepository.findAll()).willReturn(new ArrayList<>());

        // when
        Round result = underTestService.create(round);

        // then
        ArgumentCaptor<Round> argumentCaptor = ArgumentCaptor.forClass(Round.class);
        verify(roundRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(round);
        assertThat(result).isEqualTo(round);
    }

    @Test
    void createRound_WithNullInput_ThrowsException() {
        // given
        Round round = null;

        // when
        assertThatThrownBy(() -> underTestService.create(round))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed round is null");

        // then
        verify(roundRepository, never()).save(any());
    }

    @Test
    void createRound_ThatAlreadyExists_ThrowsException() {
        // given
        League contest = new League("Premier League", LocalDateTime.now());
        Round round = new Round(1, LocalDateTime.now(), contest);
        given(roundRepository.findAll()).willReturn(Arrays.asList(round));

        // when
        assertThatThrownBy(() -> underTestService.create(round))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Round with given data already exists in the database");

        // then
        verify(roundRepository, never()).save(any());
    }

    @Test
    void deleteRound_WithValidId_DeletesRound() {
        // given
        Long validId = 1L;
        given(roundRepository.existsById(validId.toString())).willReturn(true);

        // when
        boolean result = underTestService.delete(validId);

        // then
        verify(roundRepository).deleteById(validId.toString());
        assertThat(result).isTrue();
    }

    @Test
    void deleteRound_WithInvalidId_ThrowsException() {
        // given
        Long invalidId = 0L;

        // when
        assertThatThrownBy(() -> underTestService.delete(invalidId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(roundRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteRound_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 2L;
        given(roundRepository.existsById(nonExistingId.toString())).willReturn(false);

        // when
        assertThatThrownBy(() -> underTestService.delete(nonExistingId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Round with id: " + nonExistingId + " not found in the database.");

        // then
        verify(roundRepository, never()).deleteById(nonExistingId.toString());
    }

    @Test
    void updateRound_WithValidInput_ReturnsUpdatedRound() {
        // given
        Long validId = 1L;
        League contest = new League("Premier League", LocalDateTime.now());
        Round round = new Round(1, LocalDateTime.now(), contest);
        Round updatedRound = new Round(2, LocalDateTime.now().plusDays(1), contest);
        given(roundRepository.findById(String.valueOf(validId))).willReturn(Optional.of(round));
        given(roundRepository.save(round)).willReturn(updatedRound);

        // when
        Round result = underTestService.update(validId, updatedRound);

        // then
        verify(roundRepository).findById(String.valueOf(validId));
        verify(roundRepository).save(round);
        assertThat(result).isEqualTo(updatedRound);
    }

    @Test
    void updateRound_WithInvalidId_ThrowsException() {
        // given
        Long invalidId = 0L;
        Round round = new Round(1, LocalDateTime.now(), new League("Premier League", LocalDateTime.now()));

        // when
        assertThatThrownBy(() -> underTestService.update(invalidId, round))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(roundRepository, never()).findById(anyString());
        verify(roundRepository, never()).save(any());
    }

    @Test
    void updateRound_WithNullInput_ThrowsException() {
        // given
        Long validId = 1L;
        Round round = null;

        // when
        assertThatThrownBy(() -> underTestService.update(validId, round))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New round is null.");

        // then
        verify(roundRepository, never()).findById(anyString());
        verify(roundRepository, never()).save(any());
    }

    @Test
    void updateRound_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 2L;
        Round round = new Round(1, LocalDateTime.now(), new League("Premier League", LocalDateTime.now()));
        given(roundRepository.findById(String.valueOf(nonExistingId))).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> underTestService.update(nonExistingId, round))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Round with given id was not found in the database.");

        // then
        verify(roundRepository).findById(String.valueOf(nonExistingId));
        verify(roundRepository, never()).save(any());
    }

    @Test
    void getById_WithValidId_ReturnsRound() {
        // given
        Long validId = 1L;
        Round round = new Round(1, LocalDateTime.now(), new League("Premier League", LocalDateTime.now()));
        given(roundRepository.findById(String.valueOf(validId))).willReturn(Optional.of(round));

        // when
        Round result = underTestService.getById(validId);

        // then
        assertThat(result).isEqualTo(round);
    }

    @Test
    void getById_WithInvalidId_ThrowsException() {
        // given
        Long invalidId = 0L;

        // when
        assertThatThrownBy(() -> underTestService.getById(invalidId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(roundRepository, never()).findById(anyString());
    }

    @Test
    void getById_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 2L;
        given(roundRepository.findById(String.valueOf(nonExistingId))).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> underTestService.getById(nonExistingId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Round with given id was not found in the database.");

        // then
        verify(roundRepository).findById(String.valueOf(nonExistingId));
    }

    @Test
    void getByLeagueId_WithValidLeagueId_ReturnsRounds() {
        // given
        Long validLeagueId = 1L;
        League league = new League("Premier League", LocalDateTime.now());
        Round round = new Round(1, LocalDateTime.now(), league);
        List<Round> rounds = List.of(round);
        given(leagueRepository.findById(validLeagueId)).willReturn(Optional.of(league));
        given(roundRepository.findByContest(league)).willReturn(Optional.of(rounds));

        // when
        List<Round> result = underTestService.getByLeagueId(validLeagueId);

        // then
        assertThat(result).isEqualTo(rounds);
    }

    @Test
    void getByLeagueId_WithInvalidLeagueId_ThrowsException() {
        // given
        Long invalidLeagueId = 0L;

        // when
        assertThatThrownBy(() -> underTestService.getByLeagueId(invalidLeagueId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(leagueRepository, never()).findById(anyLong());
        verify(roundRepository, never()).findByContest(any());
    }

    @Test
    void getByLeagueId_WithNonExistingLeagueId_ThrowsException() {
        // given
        Long nonExistingLeagueId = 2L;
        given(leagueRepository.findById(nonExistingLeagueId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> underTestService.getByLeagueId(nonExistingLeagueId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("League not found in the database");

        // then
        verify(leagueRepository).findById(nonExistingLeagueId);
        verify(roundRepository, never()).findByContest(any());
    }

    @Test
    void getByLeagueId_WithExistingLeagueButNoRounds_ThrowsException() {
        // given
        Long leagueId = 3L;
        League league = new League("Premier League", LocalDateTime.now());
        given(leagueRepository.findById(leagueId)).willReturn(Optional.of(league));
        given(roundRepository.findByContest(league)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> underTestService.getByLeagueId(leagueId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Contest with given id was not found in the database.");

        // then
        verify(leagueRepository).findById(leagueId);
        verify(roundRepository).findByContest(league);
    }

    @Test
    void checkIfEntityExistsInDb_WithExistingEntityId_ReturnsTrue() {
        // given
        Long existingEntityId = 1L;
        League contest = new League("Premier League", LocalDateTime.now());
        Round round = new Round(1, LocalDateTime.now(), contest);
        round.setUuid(existingEntityId);
        List<Round> rounds = List.of(round);
        given(roundRepository.findAll()).willReturn(rounds);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(existingEntityId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void checkIfEntityExistsInDb_WithNonExistingEntityId_ReturnsFalse() {
        // given
        Long nonExistingEntityId = 2L;
        League contest = new League("Premier League", LocalDateTime.now());
        Round round = new Round(1, LocalDateTime.now(), contest);
        round.setUuid(1L);
        List<Round> rounds = List.of(round);
        given(roundRepository.findAll()).willReturn(rounds);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(nonExistingEntityId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void checkIfEntityExistsInDb_WithEmptyRepository_ReturnsFalse() {
        // given
        Long entityId = 1L;
        given(roundRepository.findAll()).willReturn(new ArrayList<>());

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(entityId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void getAll_ReturnsListOfRounds() {
        // given
        League contest = new League("Premier League", LocalDateTime.now());
        Round round1 = new Round(1, LocalDateTime.now(), contest);
        Round round2 = new Round(2, LocalDateTime.now().plusDays(1), contest);
        List<Round> rounds = List.of(round1, round2);
        given(roundRepository.findAll()).willReturn(rounds);

        // when
        List<Round> result = underTestService.getAll();

        // then
        assertThat(result).isEqualTo(rounds);
    }

    @Test
    void getAll_WithEmptyRepository_ReturnsEmptyList() {
        // given
        given(roundRepository.findAll()).willReturn(new ArrayList<>());

        // when
        List<Round> result = underTestService.getAll();

        // then
        assertThat(result.isEmpty());
    }
}