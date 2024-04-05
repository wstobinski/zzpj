package com.handballleague.configs;

import com.handballleague.model.POSITIONS;
import com.handballleague.model.Player;
import com.handballleague.model.Position;
import com.handballleague.repositories.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PlayerConfig {
//    @Bean
//    CommandLineRunner commandLineRunner(PlayerRepository repository) {
//        return args -> {
//            Player player1 = new Player("John",
//                    "Doe",
//                    "1234567890",
//                    7,
////                    new Position(POSITIONS.CENTER_BACK),
//                    true,
//                    false);
//            Player player2 = new Player("Jane",
//                    "Smith",
//                    "0987654321",
//                    5,
////                    new Position(POSITIONS.RIGHT_WING),
//                    false,
//                    true);
//
//            repository.saveAll(List.of(player1,player2));
//        };
//    }
}
