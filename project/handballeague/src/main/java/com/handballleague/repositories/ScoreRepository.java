package com.handballleague.repositories;

import com.handballleague.model.Match;
import com.handballleague.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<List<Score>> findByMatch(Match match);

    @Query("SELECT s FROM Score s WHERE s.team.uuid = :teamId1 AND s.match IN (SELECT s1.match FROM Score s1 WHERE s1.team.uuid = :teamId1) AND s.match IN (SELECT s2.match FROM Score s2 WHERE s2.team.uuid = :teamId2)")
    List<Score> findScoresByTeams(@Param("teamId1") Long teamId1, @Param("teamId2") Long teamId2);
}