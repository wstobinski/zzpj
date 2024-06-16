package com.handballleague.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class CommentTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void testCommentEntityMappings() {
        // Given
        Comment comment = new Comment();
        comment.setContent("Test comment");
        comment.setSentimentScore(0.0);

        // When
        entityManager.persist(comment);
        entityManager.flush();
        entityManager.clear();

        // Then
        Comment savedComment = entityManager.find(Comment.class, comment.getId());
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("Test comment");
        assertThat(savedComment.getCreatedDate()).isNotNull(); // Default value set by LocalDateTime.now()
        assertThat(savedComment.isEdited()).isFalse(); // Default value should be false
        assertThat(savedComment.getSentimentScore()).isEqualTo(0.0);
    }

    @Test
    void testCommentContentCannotBeNull() {
        // Given
        Comment comment = new Comment();

        // When
        // Attempt to persist the Comment with null content
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
            entityManager.persist(comment);
            entityManager.flush();
        });
    }

    @Test
    void testCommentSentimentScoreRange() {
        // Given
        Comment comment = new Comment();
        comment.setContent("Test comment");

        // When
        // Attempt to persist the Comment with an out-of-range sentiment score
        comment.setSentimentScore(6.0f);

        // Then
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(comment);
            entityManager.flush();
        });
    }
}