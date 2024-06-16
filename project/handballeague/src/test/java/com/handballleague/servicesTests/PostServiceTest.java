package com.handballleague.servicesTests;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Post;
import com.handballleague.repositories.PostRepository;
import com.handballleague.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;


    private PostService underTestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTestService = new PostService(postRepository);
    }

    @Test
    void create_ValidPost_ReturnsCreatedPost() throws InvalidArgumentException, EntityAlreadyExistsException {
        // Given
        Post newPost = Post.builder().title("Test Post")
                .content("Test Content")
                .postedDate(LocalDateTime.now()).build();
        when(postRepository.save(any(Post.class))).thenReturn(newPost);

        // When
        Post createdPost = underTestService.create(newPost);

        // Then
        assertThat(createdPost).isEqualTo(newPost);
        assertThat(createdPost.getPostedDate()).isNotNull();
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void create_NullPost_ThrowsInvalidArgumentException() {
        // When/Then
        assertThatThrownBy(() -> underTestService.create(null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Passed parameter is invalid");
        verify(postRepository, never()).save(any());
    }

    @Test
    void create_DuplicatePost_ThrowsEntityAlreadyExistsException() {
        // Given
        Post existingPost = Post.builder().title("Test Post")
                .content("Test Content")
                .postedDate(LocalDateTime.now()).build();
        when(postRepository.findAll()).thenReturn(List.of(existingPost));

        // When/Then
        assertThatThrownBy(() -> underTestService.create(existingPost))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("Post with given data already exists in the database");
        verify(postRepository, never()).save(any());
    }

    @Test
    void create_EmptyTitle_ThrowsInvalidArgumentException() {
        // Given
        Post postWithNoTitle = Post.builder().title("")
                .content("Test Content")
                .postedDate(LocalDateTime.now()).build();

        // When/Then
        assertThatThrownBy(() -> underTestService.create(postWithNoTitle))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Post content cannot be empty and title must be specified.");
        verify(postRepository, never()).save(any());
    }


    @Test
    void delete_ValidId_DeletesPost() {
        // Given
        Long postId = 1L;
        given(postRepository.existsById(postId)).willReturn(true);

        // When
        boolean result = underTestService.delete(postId);

        // Then
        assertThat(result).isTrue();
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void delete_InvalidId_ThrowsInvalidArgumentException() {
        // When/Then
        assertThatThrownBy(() -> underTestService.delete(-1L))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Passed id is invalid.");
        verify(postRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_NonExistingPost_ThrowsObjectNotFoundInDataBaseException() {
        // Given
        Long nonExistingId = 999L;

        // When/Then
        assertThatThrownBy(() -> underTestService.delete(nonExistingId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessage("Post with id: " + nonExistingId + " not found in the database.");
        verify(postRepository, never()).deleteById(anyLong());
    }
    @Test
    void update_ValidIdAndPost_ReturnsUpdatedPost() throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        // Given
        Long postId = 1L;
        Post existingPost = Post.builder().title("Test")
                .content("Test Content")
                .postedDate(LocalDateTime.now()).build();
        Post updatedPost = Post.builder().title("Test2")
                .content("Test Content")
                .postedDate(LocalDateTime.now()).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

        // When
        Post result = underTestService.update(postId, updatedPost);

        // Then
        assertThat(result).isEqualTo(updatedPost);
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void update_InvalidId_ThrowsInvalidArgumentException() {
        // When/Then
        assertThatThrownBy(() -> underTestService.update(-1L, new Post()))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Passed id is invalid.");
        verify(postRepository, never()).findById(anyLong());
        verify(postRepository, never()).save(any());
    }

    @Test
    void update_NullPost_ThrowsInvalidArgumentException() {
        // When/Then
        assertThatThrownBy(() -> underTestService.update(1L, null))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("New post is null.");
        verify(postRepository, never()).findById(anyLong());
        verify(postRepository, never()).save(any());
    }

    @Test
    void update_PostWithNullContent_ThrowsInvalidArgumentException() {
        // Given
        Long postId = 1L;

        // When/Then
        assertThatThrownBy(() -> underTestService.update(postId, new Post()))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("New posts content is null.");
        verify(postRepository, never()).findById(postId);
        verify(postRepository, never()).save(any());
    }
    @Test
    void update_PostWithoutContent_ThrowsInvalidArgumentException() {
        // Given
        Long postId = 1L;

        // When/Then
        assertThatThrownBy(() -> underTestService.update(postId, Post.builder().content("").build()))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Post content cannot be empty.");
        verify(postRepository, never()).findById(postId);
        verify(postRepository, never()).save(any());
    }

    @Test
    void update_PostNotFound_ThrowsObjectNotFoundInDataBaseException() {
        // Given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> underTestService.update(postId, Post.builder().content("New content").build()))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessage("Post with given id was not found in the database.");
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any());
    }

    @Test
    void getById_ValidId_ReturnsPost() throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        // Given
        Long postId = 1L;
        Post expectedPost = Post.builder().title("Test")
                .content("Test Content")
                .postedDate(LocalDateTime.now()).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(expectedPost));

        // When
        Post result = underTestService.getById(postId);

        // Then
        assertThat(result).isEqualTo(expectedPost);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getById_InvalidId_ThrowsInvalidArgumentException() {
        // When/Then
        assertThatThrownBy(() -> underTestService.getById(-1L))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Passed id is invalid.");
        verify(postRepository, never()).findById(anyLong());
    }

    @Test
    void getById_PostNotFound_ThrowsObjectNotFoundInDataBaseException() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> underTestService.getById(postId))
                .isInstanceOf(ObjectNotFoundInDataBaseException.class)
                .hasMessage("Post with given id was not found in the database.");
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getAll_ReturnsAllPostsSortedByPostedDateDesc() {
        // Given
        List<Post> expectedPosts = new ArrayList<>();
        expectedPosts.add(new Post(1L, "Post 1", "Content 1", LocalDateTime.now()));
        expectedPosts.add(new Post(2L, "Post 2", "Content 2", LocalDateTime.now().minusHours(1)));
        expectedPosts.add(new Post(3L, "Post 3", "Content 3", LocalDateTime.now().minusDays(1)));
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "postedDate");

        when(postRepository.findAll(sortByCreatedDate)).thenReturn(expectedPosts);

        // When
        List<Post> result = underTestService.getAll();

        // Then
        assertThat(result).isEqualTo(expectedPosts);
        verify(postRepository, times(1)).findAll(sortByCreatedDate);
    }

    @Test
    void getAll_NoPosts_ReturnsEmptyList() {
        // Given
        List<Post> emptyList = new ArrayList<>();
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "postedDate");
        when(postRepository.findAll(sortByCreatedDate)).thenReturn(emptyList);

        // When
        List<Post> result = underTestService.getAll();

        // Then
        assertThat(result.size()).isEqualTo(0);
        verify(postRepository, times(1)).findAll(sortByCreatedDate);
    }
    @Test
    void checkIfEntityExistsInDb_ExistingEntityId_ReturnsTrue() {
        // Given
        Long existingId = 1L;
        when(postRepository.existsById(existingId)).thenReturn(true);

        // When
        boolean result = underTestService.checkIfEntityExistsInDb(existingId);

        // Then
        assertThat(result).isTrue();
        verify(postRepository, times(1)).existsById(existingId);
    }

    @Test
    void checkIfEntityExistsInDb_NonExistingEntityId_ReturnsFalse() {
        // Given
        Long nonExistingId = 999L;
        when(postRepository.existsById(nonExistingId)).thenReturn(false);

        // When
        boolean result = underTestService.checkIfEntityExistsInDb(nonExistingId);

        // Then
        assertThat(result).isFalse();
        verify(postRepository, times(1)).existsById(nonExistingId);
    }
}