package com.lab.players;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PlayersLabApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(PlayersLabApplication.class, args);
            log.info("Application started");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error on start application", e);
            System.exit(-1);
        }
    }
}
