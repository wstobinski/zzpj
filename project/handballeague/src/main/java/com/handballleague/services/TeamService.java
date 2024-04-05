package com.handballleague.services;

import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    public void addNewTeam(Team team) {
        teamRepository.save(team);
    }

    public Team addPlayerToTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Player player = playerRepository.findById(String.valueOf(playerId))
                .orElseThrow(() -> new RuntimeException("Player not found"));

        team.getPlayers().add(player);

        teamRepository.save(team);

        return team;
    }

    public void deleteTeam(Long id) {
        if(teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Team with id: " + id + " not found.");
        }
    }

}
