package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @OneToOne
    @JoinColumn(name = "match_id", referencedColumnName = "uuid")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "uuid")
    private Team team;

    @Column(
            name = "goals",
            nullable = false
    )
    @Size(min = 0, max = 81, message = "Goals need to be within the range of [0,81].")
    private int goals;

    @Column(
            name = "lost_goals",
            nullable = false
    )
    @Size(min = 0, max = 81, message = "Lost goals need to be within the range of [0,81].")
    private int lostGoals;

    @Column(
            name = "fouls",
            nullable = false
    )
    @Size(min = 0, max = 100, message = "Fouls need to be within the range of [0,100].")
    private int fouls;

    @Column(
            name = "ball_possession",
            nullable = false
    )
    @Size(min = 0, max = 100, message = "Ball possession needs to be within the range of [0,100]%.")
    private int ballPossession;

    @Column(
            name = "yellow_cards",
            nullable = false
    )
    @Size(min = 0, max = 10, message = "Yellow cards need to be within the range of [0,10].")
    private int yellowCards;

    @Column(
            name = "red_cards",
            nullable = false
    )
    @Size(min = 0, max = 3, message = "Red cards need to be within the range of [0,3].")
    private int redCards;

    @Column(
            name = "time_penalties",
            nullable = false
    )
    @Size(min = 0, max = 60, message = "Time penalties need to be within the range of [0,60].")
    private int timePenalties;
}