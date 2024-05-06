package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "score")
public class Score {
    @Id
    private Long uuid;

    @ManyToOne
    @JoinColumn(name = "match_id", referencedColumnName = "uuid")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "uuid")
    private Team team;

    @Column(name = "goals", nullable = false)
    @Min(0)
    @Max(81)
    private int goals;

    @Column(name = "lost_goals", nullable = false)
    @Min(0)
    @Max(81)
    private int lostGoals;

    @Column(name = "fouls", nullable = false)
    @Min(0)
    @Max(100)
    private int fouls;

    @Column(name = "ball_possession", nullable = false)
    @Min(0)
    @Max(100)
    private int ballPossession;

    @Column(name = "yellow_cards", nullable = false)
    @Min(0)
    @Max(10)
    private int yellowCards;

    @Column(name = "red_cards", nullable = false)
    @Min(0)
    @Max(3)
    private int redCards;

    @Column(name = "time_penalties", nullable = false)
    @Min(0)
    @Max(60)
    private int timePenalties;
}
