package com.handballleague.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "team_contest")
public class TeamContest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "team_id", referencedColumnName = "uuid")
    private Team teams;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "league_id", referencedColumnName = "uuid")
    private League league;

    @Column(nullable = false)
    private int points;
    @Column(nullable = false)
    private int goalsAcquired;
    @Column(nullable = false)
    private int goalsLost;
    @Transient
    private int gamesPlayed;
    @Column(nullable = false)
    private int wins;
    @Column(nullable = false)
    private int draws;
    @Column(nullable = false)
    private int losses;

    public TeamContest(Team teams, League league, int points, int goalsAcquired, int goalsLost, int wins, int draws, int losses) {
        this.teams = teams;
        this.league = league;
        this.points = points;
        this.goalsAcquired = goalsAcquired;
        this.goalsLost = goalsLost;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
    }

    public int getGamesPlayed() {
        return wins + draws + losses;
    }
}
