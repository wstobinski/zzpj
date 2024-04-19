package com.handballleague.model.ids;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TeamContestID implements Serializable {
    private  Long team;
    private  Long league;

    public TeamContestID(Long team, Long league) {
        this.team = team;
        this.league = league;
    }

    public TeamContestID() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamContestID that = (TeamContestID) o;
        return team.equals(that.team) && league.equals(that.league);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, league);
    }
}
