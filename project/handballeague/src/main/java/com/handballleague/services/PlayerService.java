package com.handballleague.services;

import com.handballleague.model.Player;
import com.handballleague.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    public void addNewPlayer(Player player) {
        playerRepository.save(player);
    }

    public void deletePlayer(Long id) {
        if(playerRepository.existsById(Long.toString(id))) {
            playerRepository.deleteById(Long.toString(id));
        } else {
            throw new IllegalStateException("Player with id: " + id + " not found.");
        }
    }
}
