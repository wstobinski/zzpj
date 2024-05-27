package com.handballleague.model;

import com.handballleague.model.ids.TeamContestID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor

@Getter
@Setter
@Entity
@IdClass(TeamContestID.class)
@Table(name = "team_contest")
public class TeamContest {

    @Id
    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "uuid")
    private Team team;

    @Id
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "uuid")
    private League league;

    @Column(nullable = false)
    @Min(value = 0, message = "Points cannot be negative")
    private int points;
    @Column(nullable = false)
    @Min(value = 0, message = "GoalsAcquired cannot be negative")
    private int goalsAcquired;
    @Column(nullable = false)
    @Min(value = 0, message = "GoalsLost cannot be negative")
    private int goalsLost;
    @Transient
    private int gamesPlayed;
    @Column(nullable = false)
    @Min(value = 0, message = "Number of wins cannot be negative")
    private int wins;
    @Column(nullable = false)
    @Min(value = 0, message = "Number of draws cannot be negative")
    private int draws;
    @Column(nullable = false)
    @Min(value = 0, message = "Number of losses cannot be negative")
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

