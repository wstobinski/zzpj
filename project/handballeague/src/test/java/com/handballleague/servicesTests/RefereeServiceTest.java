package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Referee;
import com.handballleague.repositories.MatchRepository;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.RefereeRepository;
import com.handballleague.repositories.TeamRepository;
import com.handballleague.services.PlayerService;
import com.handballleague.services.RefereeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class RefereeServiceTest {

    @Mock
    private RefereeRepository refereeRepository;
    @Mock
    private MatchRepository matchRepository;

    private AutoCloseable autoCloseable;
    private RefereeService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new RefereeService(refereeRepository, matchRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createReferee_WithValidInput_ReturnsReferee() {
        // given
        Referee r = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);

        // when
        underTestService.create(r);

        // then
        ArgumentCaptor<Referee> argumentCaptor = ArgumentCaptor.forClass(Referee.class);
        verify(refereeRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(r);
    }

    @Test
    void createReferee_WithNullInput_ThrowsException() {
        // given
        Referee r = null;

        // when
        assertThatThrownBy(() -> underTestService.create(r))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed parameter is invalid");

        // then
        verify(refereeRepository, never()).save(any());
    }

    @Test
    void createReferee_ThatAlreadyExists_ThrowsException() {
        // given
        Referee r = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);


        given(refereeRepository.findAll()).willReturn(Collections.singletonList(r));


        // when
        assertThatThrownBy(() -> underTestService.create(r))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Referee with given data already exists in the database");

        // then
        verify(refereeRepository, never()).save(any());
    }

    @Test
    void createReferee_WithInvalidParameters_ThrowsException() {
        // given
        Referee r1 = new Referee(1L, "", "Smith", "123456789", "john.smith@example.com", 4.5);
        Referee r2 = new Referee(1L, "John", "", "123456789", "john.smith@example.com", 4.5);
        Referee r3 = new Referee(1L, "John", "Smith", "123456789", "", 4.5);

        // when
        assertThatThrownBy(() -> underTestService.create(r1))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of referee parameters is invalid.");

        assertThatThrownBy(() -> underTestService.create(r2))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of referee parameters is invalid.");

        assertThatThrownBy(() -> underTestService.create(r3))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of referee parameters is invalid.");

        // then
        verify(refereeRepository, never()).save(any());
    }

    @Test
    void deleteReferee_WithValidId_DeletesReferee() {
        // given
        Long validId = 1L;
        given(refereeRepository.existsById(validId)).willReturn(true);

        // when
        boolean result = underTestService.delete(validId);

        // then
        verify(refereeRepository).deleteById(validId);
        assertThat(result).isTrue();
    }

    @Test
    void deleteReferee_WithInvalidId_ThrowsException() {
        // given
        Long invalidId = 0L;

        // when
        assertThatThrownBy(() -> underTestService.delete(invalidId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(refereeRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteReferee_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 2L;
        given(refereeRepository.existsById(nonExistingId)).willReturn(false);

        // when
        assertThatThrownBy(() -> underTestService.delete(nonExistingId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Referee with id: " + nonExistingId + " not found in the database.");

        // then
        verify(refereeRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateReferee_WithValidIdAndEntity_UpdatesReferee() {
        // given
        Long validId = 1L;
        Referee existingReferee = new Referee(1L, "Jane", "Doe", "987654321", "jane.doe@example.com", 4.2);
        Referee updatedReferee = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);

        given(refereeRepository.findById(validId)).willReturn(Optional.of(existingReferee));
        given(refereeRepository.save(any(Referee.class))).willReturn(updatedReferee);

        // when
        Referee result = underTestService.update(validId, updatedReferee);

        // then
        ArgumentCaptor<Referee> argumentCaptor = ArgumentCaptor.forClass(Referee.class);
        verify(refereeRepository).save(argumentCaptor.capture());
        Referee capturedReferee = argumentCaptor.getValue();

        assertThat(capturedReferee.getFirstName()).isEqualTo(updatedReferee.getFirstName());
        assertThat(capturedReferee.getLastName()).isEqualTo(updatedReferee.getLastName());
        assertThat(capturedReferee.getPhoneNumber()).isEqualTo(updatedReferee.getPhoneNumber());
        assertThat(capturedReferee.getEmail()).isEqualTo(updatedReferee.getEmail());
        assertThat(result).isEqualTo(updatedReferee);
    }

    @Test
    void updateReferee_WithInvalidId_ThrowsException() {
        // given
        Long invalidId = 0L;
        Referee updatedReferee = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);

        // when
        assertThatThrownBy(() -> underTestService.update(invalidId, updatedReferee))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(refereeRepository, never()).save(any(Referee.class));
    }

    @Test
    void updateReferee_WithNullEntity_ThrowsException() {
        // given
        Long validId = 1L;
        Referee updatedReferee = null;

        // when
        assertThatThrownBy(() -> underTestService.update(validId, updatedReferee))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New referee is null.");

        // then
        verify(refereeRepository, never()).save(any(Referee.class));
    }

    @Test
    void updateReferee_WithInvalidParameters_ThrowsException() {
        // given
        Long validId = 1L;
        Referee invalidReferee1 = new Referee(1L, "", "Smith", "123456789", "john.smith@example.com", 4.5);
        Referee invalidReferee2 = new Referee(1L, "John", "", "123456789", "john.smith@example.com", 4.5);
        Referee invalidReferee3 = new Referee(1L, "John", "Smith", "", "john.smith@example.com", 4.5);

        // when
        assertThatThrownBy(() -> underTestService.update(validId, invalidReferee1))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of referees parameters is invalid.");

        assertThatThrownBy(() -> underTestService.update(validId, invalidReferee2))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of referees parameters is invalid.");

        assertThatThrownBy(() -> underTestService.update(validId, invalidReferee3))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("At least one of referees parameters is invalid.");

        // then
        verify(refereeRepository, never()).save(any(Referee.class));
    }

    @Test
    void updateReferee_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 2L;
        Referee updatedReferee = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);
        given(refereeRepository.findById(nonExistingId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> underTestService.update(nonExistingId, updatedReferee))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Referee with given id was not found in the database.");

        // then
        verify(refereeRepository, never()).save(any(Referee.class));
    }

    @Test
    void getById_WithValidId_ReturnsReferee() {
        // given
        Long validId = 1L;
        Referee referee = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);
        given(refereeRepository.findById(validId)).willReturn(Optional.of(referee));

        // when
        Referee result = underTestService.getById(validId);

        // then
        assertThat(result).isEqualTo(referee);
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
        verify(refereeRepository, never()).findById(anyLong());
    }

    @Test
    void getById_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 2L;
        given(refereeRepository.findById(nonExistingId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> underTestService.getById(nonExistingId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Referee with given id was not found in the database.");

        // then
        verify(refereeRepository).findById(nonExistingId);
    }

    @Test
    void getAll_ReturnsSortedReferees() {
        // given
        Referee referee1 = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);
        Referee referee2 = new Referee(1L, "Jane", "Doe", "987654321", "jane.doe@example.com", 4.2);
        List<Referee> referees = List.of(referee1, referee2);
        Sort sortByRefereeId = Sort.by(Sort.Direction.ASC, "uuid");

        given(refereeRepository.findAll(sortByRefereeId)).willReturn(referees);

        // when
        List<Referee> result = underTestService.getAll();

        // then
        verify(refereeRepository).findAll(sortByRefereeId);
        assertThat(result).isEqualTo(referees);
    }

    @Test
    void checkIfEntityExistsInDb_WithExistingEntity_ReturnsTrue() {
        // given
        Long existingId = 1L;
        Referee referee = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);
        referee.setUuid(existingId);
        List<Referee> referees = List.of(referee);

        given(refereeRepository.findAll()).willReturn(referees);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(existingId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void checkIfEntityExistsInDb_WithNonExistingEntity_ReturnsFalse() {
        // given
        Long nonExistingId = 2L;
        Referee referee = new Referee(1L, "John", "Smith", "123456789", "john.smith@example.com", 4.5);
        referee.setUuid(1L);
        List<Referee> referees = List.of(referee);

        given(refereeRepository.findAll()).willReturn(referees);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(nonExistingId);

        // then
        assertThat(result).isFalse();
    }

}