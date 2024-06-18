package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.InvalidCommentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Comment;
import com.handballleague.model.Match;
import com.handballleague.model.Player;
import com.handballleague.model.Referee;
import com.handballleague.repositories.CommentRepository;
import com.handballleague.repositories.LeagueRepository;
import com.handballleague.repositories.RefereeRepository;
import com.handballleague.repositories.RoundRepository;
import com.handballleague.services.CommentService;
import com.handballleague.services.RoundService;
import com.handballleague.util.SentimentProvider;
import com.handballleague.util.Translator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ConfigurationPropertiesAutoConfiguration configurationPropertiesAutoConfiguration;
    @Mock
    private RefereeRepository refereeRepository;
    @Mock
    private Translator translator;
    @Mock
    private SentimentProvider sentimentProvider;

    private AutoCloseable autoCloseable;

    private CommentService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new CommentService(commentRepository, configurationPropertiesAutoConfiguration, refereeRepository, translator, sentimentProvider);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createComment_WithNullInput_ThrowsException() {
        // given
        Comment comment = null;

        // when
        assertThatThrownBy(() -> underTestService.create(comment))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed parameter is invalid");

        // then
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WithExistingComment_ThrowsException() {
        // given
        Comment existingComment = new Comment("Nice match!", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", 1), new Match());
        List<Comment> comments = new ArrayList<>();
        comments.add(existingComment);
        given(commentRepository.findAll()).willReturn(comments);

        // when
        assertThatThrownBy(() -> underTestService.create(existingComment))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Comment with given data already exists in the database");

        // then
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WithEmptyContent_ThrowsException() {
        // given
        Comment comment = new Comment("", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", 0), new Match());

        // when
        assertThatThrownBy(() -> underTestService.create(comment))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Comment content cannot be empty");

        // then
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WithNullAuthor_ThrowsException() {
        // given
        Comment comment = new Comment("Nice match!", null, new Referee("John", "Smith", "123456789", "john.smith@example.com", 1), new Match());

        // when
        assertThatThrownBy(() -> underTestService.create(comment))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Comment content cannot be empty and author must be specified.");

        // then
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WithNullReferee_ThrowsException() {
        // given
        Comment comment = new Comment("Nice match!", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), null, new Match());

        // when
        assertThatThrownBy(() -> underTestService.create(comment))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Comment content cannot be empty and author must be specified.");

        // then
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WithNullMatch_ThrowsException() {
        // given
        Comment comment = new Comment("Nice match!", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", 1), null);

        // when
        assertThatThrownBy(() -> underTestService.create(comment))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Comment content cannot be empty and author must be specified.");

        // then
        verify(commentRepository, never()).save(any());
    }


    @Test
    void deleteComment_WithValidId_ReturnsTrue() throws ObjectNotFoundInDataBaseException, InvalidArgumentException {
        // given
        Long commentId = 1L;
        given(commentRepository.existsById(commentId)).willReturn(true);

        // when
        boolean result = underTestService.delete(commentId);

        // then
        verify(commentRepository).deleteById(commentId);
        assertThat(result).isTrue();
    }

    @Test
    void deleteComment_WithInvalidId_ThrowsException() {
        // given
        Long invalidId = 0L;

        // when
        assertThatThrownBy(() -> underTestService.delete(invalidId))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    void deleteComment_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 1L;
        given(commentRepository.existsById(nonExistingId)).willReturn(false);

        // when
        assertThatThrownBy(() -> underTestService.delete(nonExistingId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Comment with id: " + nonExistingId + " not found");

        // then
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    void updateComment_WithValidId_ReturnsUpdatedComment() throws ObjectNotFoundInDataBaseException, InvalidArgumentException {
        // given
        Long commentId = 1L;
        Comment existingComment = new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match());
        existingComment.setId(commentId);

        Comment updatedComment = new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match());
        updatedComment.setId(commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(existingComment));
        given(commentRepository.save(existingComment)).willReturn(updatedComment);

        // when
        Comment result = underTestService.update(commentId, updatedComment);

        // then
        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(existingComment);
        assertThat(result.getContent()).isEqualTo(updatedComment.getContent());
        assertThat(result.isEdited()).isFalse();
    }

    @Test
    void updateComment_WithInvalidId_ThrowsException() {
        // given
        Long invalidId = 0L;
        Comment comment = new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match());

        // when
        assertThatThrownBy(() -> underTestService.update(invalidId, comment))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Passed id is invalid.");

        // then
        verify(commentRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_WithNullComment_ThrowsException() {
        // given
        Long commentId = 1L;

        // when
        assertThatThrownBy(() -> underTestService.update(commentId, null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("New comment is null.");

        // then
        verify(commentRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_WithEmptyContent_ThrowsException() {
        // given
        Long commentId = 1L;
        Comment comment = new Comment("", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match());

        // when
        assertThatThrownBy(() -> underTestService.update(commentId, comment))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessageContaining("Comment content cannot be empty.");

        // then
        verify(commentRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getById_WithValidId_ReturnsComment() throws ObjectNotFoundInDataBaseException, InvalidArgumentException {
        // given
        Long commentId = 1L;
        Comment comment = new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match());
        comment.setId(commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        Comment result = underTestService.getById(commentId);

        // then
        verify(commentRepository).findById(commentId);
        assertThat(result).isEqualTo(comment);
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
        verify(commentRepository, never()).findById(any());
    }

    @Test
    void getById_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 1L;
        given(commentRepository.findById(nonExistingId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> underTestService.getById(nonExistingId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessageContaining("Comment with given id was not found");

        // then
        verify(commentRepository).findById(nonExistingId);
    }

    @Test
    void getAll_ReturnsListOfComments() {
        // given
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match()));
        comments.add(new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match()));
        given(commentRepository.findAll(any(Sort.class))).willReturn(comments);

        // when
        List<Comment> result = underTestService.getAll();

        // then
        verify(commentRepository).findAll(any(Sort.class));
        assertThat(result.size() == 2);
    }

    @Test
    void checkIfEntityExistsInDb_WithExistingComment_ReturnsTrue() {
        // given
        Comment comment = new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match());

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        given(commentRepository.findAll()).willReturn(comments);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(comment);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void checkIfEntityExistsInDb_WithNonExistingComment_ReturnsFalse() {
        // given
        Comment comment = new Comment("Bad match...", new Player("John",
                "Smith",
                "888999777",
                16,
                true,
                false), new Referee("John", "Smith", "123456789", "john.smith@example.com", -0.3), new Match());

        List<Comment> comments = new ArrayList<>();
        given(commentRepository.findAll()).willReturn(comments);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(comment);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void checkIfEntityExistsInDb_WithExistingId_ReturnsTrue() {
        // given
        Long commentId = 1L;
        given(commentRepository.existsById(commentId)).willReturn(true);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(commentId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void checkIfEntityExistsInDb_WithNonExistingId_ReturnsFalse() {
        // given
        Long nonExistingId = 1L;
        given(commentRepository.existsById(nonExistingId)).willReturn(false);

        // when
        boolean result = underTestService.checkIfEntityExistsInDb(nonExistingId);

        // then
        assertThat(result).isFalse();
    }

}