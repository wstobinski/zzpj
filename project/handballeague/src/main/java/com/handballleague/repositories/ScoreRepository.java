package com.handballleague.repositories;

import com.handballleague.model.Match;
import com.handballleague.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<List<Score>> findByMatch(Match match);
}
