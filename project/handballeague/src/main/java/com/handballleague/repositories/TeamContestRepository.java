package com.handballleague.repositories;

import com.handballleague.model.TeamContest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamContestRepository extends JpaRepository<TeamContest, Long> {
//    TeamContest findByTeamIdAndLeagueId(Long teamId, Long uuid);
}
