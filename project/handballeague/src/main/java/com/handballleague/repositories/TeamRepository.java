package com.handballleague.repositories;

import com.handballleague.model.Player;
import com.handballleague.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
