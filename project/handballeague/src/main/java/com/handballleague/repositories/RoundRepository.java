package com.handballleague.repositories;

import com.handballleague.model.League;
import com.handballleague.model.Round;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public interface RoundRepository extends JpaRepository<Round, String> {

    Optional<List<Round>> findByContest(League contest);
}
