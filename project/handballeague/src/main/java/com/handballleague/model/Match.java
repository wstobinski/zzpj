package com.handballleague.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "match")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @Column
    private LocalDateTime gameDate;

    @ManyToOne
    @JoinColumn(name = "home_team_id", referencedColumnName = "uuid")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id", referencedColumnName = "uuid")
    private Team awayTeam;

    @ManyToOne
    @JoinColumn(name = "round_id") // Ensure this matches with the primary key name of Round
    private Round round;

    //TODO: Add referee


    public Match(LocalDateTime gameDate, Team homeTeam, Team awayTeam) {
        this.gameDate = gameDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public Match() {

    }
}