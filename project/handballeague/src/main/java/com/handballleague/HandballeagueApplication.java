package com.handballleague;

import com.handballleague.model.POSITIONS;
import com.handballleague.model.Player;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class HandballeagueApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandballeagueApplication.class, args);
    }
}
