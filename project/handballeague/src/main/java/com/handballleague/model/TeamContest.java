package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
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

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "uuid")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "uuid")
    private League league;

    @Column(nullable = false)
    @Positive(message = "Points cannot be negative")
    private int points;
    @Column(nullable = false)
    @Positive(message = "GoalsAcquired cannot be negative")
    private int goalsAcquired;
    @Column(nullable = false)
    @Positive(message = "GoalsLost cannot be negative")
    private int goalsLost;
    @Transient
    private int gamesPlayed;
    @Column(nullable = false)
    @Positive(message = "Wins number cannot be negative")
    private int wins;
    @Column(nullable = false)
    @Positive(message = "Draws number cannot be negative")
    private int draws;
    @Column(nullable = false)
    @Positive(message = "Losses number cannot be negative")
    private int losses;

    public TeamContest(Team team, League league, int points, int goalsAcquired, int goalsLost, int wins, int draws, int losses) {
        this.team = team;
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
    public void updateTo(TeamContest newTeamContest) {
        if (newTeamContest != null) {
            this.setPoints(newTeamContest.getPoints());
            this.setGoalsAcquired(newTeamContest.getGoalsAcquired());
            this.setGoalsLost(newTeamContest.getGoalsLost());
            this.setWins(newTeamContest.getWins());
            this.setDraws(newTeamContest.getDraws());
            this.setLosses(newTeamContest.getLosses());
        }



    }
}
