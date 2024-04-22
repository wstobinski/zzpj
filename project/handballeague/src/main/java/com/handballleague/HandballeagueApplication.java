package com.handballleague;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class HandballeagueApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandballeagueApplication.class, args);
    }
}
