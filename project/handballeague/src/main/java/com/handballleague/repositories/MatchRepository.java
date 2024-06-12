package com.handballleague.repositories;

import com.handballleague.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE (m.homeTeam.uuid = :team1Id AND m.awayTeam.uuid = :team2Id) OR (m.homeTeam.uuid = :team2Id AND m.awayTeam.uuid = :team1Id)")
    List<Match> findByHomeTeamUuidAndAwayTeamUuid(@Param("team1Id") Long team1Id, @Param("team2Id") Long team2Id);
}