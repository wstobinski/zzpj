package com.handballleague.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "match")
public class Match {
    @Id
    private Long uuid;


    @ManyToOne
    @JoinColumn(name = "round_id")
    @JsonBackReference
    private Round round;

    @Column
    private LocalDateTime gameDate;

    @ManyToOne
    @JoinColumn(name = "home_team_id", referencedColumnName = "uuid")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id", referencedColumnName = "uuid")
    private Team awayTeam;

    @ManyToOne
    @JoinColumn(name = "referee_id", referencedColumnName = "uuid")
    private Referee referee;

    @Column
    private boolean isFinished = false;


    public Match(Long uuid,LocalDateTime gameDate, Team homeTeam, Team awayTeam) {
        this.uuid =uuid;
        this.gameDate = gameDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public Match() {

    }

    @Override
    public String toString() {
        return "Match{" +
                "uuid=" + uuid +
                ", gameDate=" + gameDate +
                ", homeTeam=" + homeTeam.getUuid() +
                ", awayTeam=" + awayTeam.getUuid() +
                '}';
    }
}