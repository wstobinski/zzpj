package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService implements HandBallService<Team>{
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Team> getAll() {
        return teamRepository.findAll();
    }


    @Override
    public Team getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Object with given id was not found in database.");

        return optionalTeam.get();
    }

    @Override
    public Team create(Team team) throws InvalidArgumentException, EntityAlreadyExistsException {
        if(team == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(team)) throw new EntityAlreadyExistsException("Team with given data already exists in database");
        if(team.getTeamName().isEmpty()) throw new InvalidArgumentException("At least one of team parameters is invalid.");

        teamRepository.save(team);

        return team;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException{
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("Team with id: " + id + " not found in database.");
        }
        return true;
    }

    @Override
    public Team update(Long id, Team newTeam) throws InvalidArgumentException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (newTeam == null)
            throw new InvalidArgumentException("New team is null.");
        if (newTeam.getTeamName().isEmpty())
            throw new InvalidArgumentException("Passed invalid arguments (team name).");

        Team teamToChange = teamRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team with given id was not found in database."));

        teamToChange.setTeamName(newTeam.getTeamName());
        teamToChange.setPlayers(newTeam.getPlayers());

        return teamRepository.save(teamToChange);
    }

    @Override
    public boolean checkIfEntityExistsInDb(Team team) {
        Iterable<Team> allTeams = teamRepository.findAll();

        for(Team t : allTeams) {
            if(team.equals(t)) {
                return true;
            }
        }
        return false;
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

    public Team removePlayerFromTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Player player = playerRepository.findById(String.valueOf(playerId))
                .orElseThrow(() -> new RuntimeException("Player not found"));

        team.getPlayers().remove(player);

        teamRepository.save(team);

        return team;
    }
}
