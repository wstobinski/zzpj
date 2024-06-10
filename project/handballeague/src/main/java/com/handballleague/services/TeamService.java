package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.LeagueRepository;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService implements HandBallService<Team>{
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository, LeagueRepository leagueRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.leagueRepository = leagueRepository;
    }

    @Override
    public List<Team> getAll() {
        Sort sortByTeamId = Sort.by(Sort.Direction.ASC, "uuid");
        return teamRepository.findAll(sortByTeamId);
    }


    @Override
    public Team getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Team> optionalTeam = teamRepository.findById(id);
        if (optionalTeam.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Team with given id was not found in the database.");

        return optionalTeam.get();
    }

    @Override
    public Team create(Team team) throws InvalidArgumentException, EntityAlreadyExistsException {
        if(team == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(team)) throw new EntityAlreadyExistsException("Team with given data already exists in the database");
        if(team.getTeamName().isEmpty()) throw new InvalidArgumentException("At least one of team parameters is invalid.");


        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException{
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(teamRepository.existsById(id)) {
            Team team = teamRepository.findById(id).orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team with given id was not found in the database."));
            List<Player> players = playerRepository.findByTeam(team);
            for (Player player : players) {
                player.setTeam(null);
                player.setCaptain(false);
                playerRepository.save(player);
            }
            teamRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("Team with id: " + id + " not found in the database.");
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
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team with given id was not found in the database."));

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

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return teamRepository.findAll().stream().filter(team -> team.getUuid().equals(entityID)).toList().size() == 1;

    }

    public Team addPlayerToTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team not found"));

        Player player = playerRepository.findById(String.valueOf(playerId))
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Player not found"));

        team.getPlayers().add(player);

        teamRepository.save(team);

        return team;
    }

    public Team removePlayerFromTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team not found"));

        Player player = playerRepository.findById(String.valueOf(playerId))
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Player not found"));

        team.getPlayers().remove(player);

        teamRepository.save(team);

        return team;
    }

    public List<Player> getAllPlayers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team not found"));
        return team.getPlayers();
    }

    public List<Team> getFreeAgents() {
        List<Team> teams = teamRepository.findAll();
        for(League l : leagueRepository.findAll()) {
            for(Team t : l.getTeams()) {
                teams.remove(t);
            }
        }
        return teams;
    }
}
