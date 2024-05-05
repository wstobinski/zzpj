package com.handballleague.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4000) // Adjust length according to expected comment size
    private String content;

    @ManyToOne
    @JoinColumn(name = "match_id", referencedColumnName = "uuid")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "referee_id", referencedColumnName = "uuid")
    private Referee referee;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "uuid")
    private Player author;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column
    private boolean isEdited = false;

    public Comment(String content, Match match, Referee referee, Player author) {
        this.content = content;
        this.match = match;
        this.referee = referee;
        this.author = author;
        this.createdDate = LocalDateTime.now();
    }
}
