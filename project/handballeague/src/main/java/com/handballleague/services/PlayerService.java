package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Player;
import com.handballleague.repositories.PlayerRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService implements HandBallService<Player>{
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    @Override
    public Player getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if(id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Player foundPlayer = playerRepository.findById(String.valueOf(id)).get();

        if (foundPlayer == null)
            throw new ObjectNotFoundInDataBaseException("Object with given id was not found in database.");

        return foundPlayer;
    }

    @Override
    public Player create(Player player) throws InvalidArgumentException, EntityAlreadyExistsException{
        if(player == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(player)) throw new EntityAlreadyExistsException("Player with given data already exists in database");
        if(player.getFirstName().isEmpty() ||
            player.getLastName().isEmpty() ||
            player.getPhoneNumber().isEmpty() ||
            player.getPitchNumber() <= 0) throw new InvalidArgumentException("At least one of players parameters is invalid.");

        playerRepository.save(player);

        return player;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException{
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(playerRepository.existsById(Long.toString(id))) {
            playerRepository.deleteById(Long.toString(id));
        } else {
            throw new ObjectNotFoundInDataBaseException("Player with id: " + id + " not found in database.");
        }
        return true;
    }

    @Override
    public Player update(Long id, Player newPlayer) throws InvalidArgumentException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (newPlayer == null)
            throw new InvalidArgumentException("New player is null.");

        Player playerToChange = playerRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Player with given id was not found in database."));

        playerToChange.setFirstName(newPlayer.getFirstName());
        playerToChange.setLastName(newPlayer.getLastName());
        playerToChange.setPhoneNumber(newPlayer.getPhoneNumber());
        playerToChange.setPitchNumber(newPlayer.getPitchNumber());
        playerToChange.setCaptain(newPlayer.isCaptain());
        playerToChange.setSuspended(newPlayer.isSuspended());

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
}
