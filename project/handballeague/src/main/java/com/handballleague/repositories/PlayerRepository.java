package com.handballleague.repositories;

import com.handballleague.model.Player;
import com.handballleague.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
    List<Player> findByTeam(Team team);

}
