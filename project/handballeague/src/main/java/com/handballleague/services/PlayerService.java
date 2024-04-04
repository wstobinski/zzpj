package com.handballleague.services;

import com.handballleague.model.POSITIONS;
import com.handballleague.model.Player;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PlayerService {
    public List<Player> getPlayers() {
        Player player1 = new Player("1", "John", "Doe", "1234567890", 7, true, POSITIONS.RIGHT_WING, false);
        Player player2 = new Player("2", "Jane", "Smith", "0987654321", 5, false, POSITIONS.LEFT_BACK, true);

        List<Player> l = new LinkedList<Player>();

        l.add(player1);
        l.add(player2);

        return l;
    }
}
