package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Column(length = 4000, nullable = false)
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

    @Column
    @DecimalMin("-1.0")
    @DecimalMax("1.0")
    private double sentimentScore;

}
