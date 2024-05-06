package com.handballleague.repositories;

import com.handballleague.model.League;
import com.handballleague.model.Team;
import com.handballleague.model.TeamContest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamContestRepository extends JpaRepository<TeamContest, Long> {
    TeamContest findByTeamAndLeague(Team team, League league);
}
