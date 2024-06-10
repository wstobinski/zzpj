package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements HandBallService<Player>{
    private final PlayerRepository playerRepository;

    private final TeamRepository teamRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public List<Player> getAll() {
        Sort sortByPlayerId = Sort.by(Sort.Direction.ASC, "uuid");
        return playerRepository.findAll(sortByPlayerId);
    }

    @Override
    public Player getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Player> optionalPlayer = playerRepository.findById(String.valueOf(id));
        if (optionalPlayer.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Player with given id was not found in the database.");

        return optionalPlayer.get();
    }

    @Override
    public Player create(Player player) throws InvalidArgumentException, EntityAlreadyExistsException{
        if(player == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(player)) throw new EntityAlreadyExistsException("Player with given data already exists in the database");
        if(player.getFirstName().isEmpty() ||
                player.getLastName().isEmpty() ||
                player.getPitchNumber() <= 0) throw new InvalidArgumentException("At least one of players parameters is invalid.");

        return playerRepository.save(player);
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException{
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(playerRepository.existsById(Long.toString(id))) {
            playerRepository.deleteById(Long.toString(id));
        } else {
            throw new ObjectNotFoundInDataBaseException("Player with id: " + id + " not found in the database.");
        }
        return true;
    }

    @Override
    public Player update(Long id, Player newPlayer) throws InvalidArgumentException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (newPlayer == null)
            throw new InvalidArgumentException("New player is null.");
        if(newPlayer.getFirstName().isEmpty() ||
                newPlayer.getLastName().isEmpty() ||
                newPlayer.getPitchNumber() <= 0) throw new InvalidArgumentException("At least one of players parameters is invalid.");

        Player playerToChange = playerRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Player with given id was not found in the database."));

        playerToChange.setFirstName(newPlayer.getFirstName());
        playerToChange.setLastName(newPlayer.getLastName());
        playerToChange.setPhoneNumber(newPlayer.getPhoneNumber());
        playerToChange.setPitchNumber(newPlayer.getPitchNumber());
        playerToChange.setCaptain(newPlayer.isCaptain());
        playerToChange.setSuspended(newPlayer.isSuspended());
        playerToChange.setEmail(newPlayer.getEmail());

        return playerRepository.save(playerToChange);
    }

    @Override
    public boolean checkIfEntityExistsInDb(Player player) {
        Iterable<Player> allPlayers = playerRepository.findAll();

        for(Player p : allPlayers) {
            if(player.equals(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return playerRepository.findAll().stream().filter(player -> player.getUuid().equals(entityID)).toList().size() == 1;

    }

    public List<Player> getFreeAgents() {
        List<Player> players= playerRepository.findAll();
        for(Team t : teamRepository.findAll()) {
            for(Player p : t.getPlayers()) {
                players.remove(p);
            }
        }
        return players;
    }
}
