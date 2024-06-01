package com.lab.players;

import lombok.extern.slf4j.Slf4j;
import me.yaman.can.webflux.h2console.H2ConsoleAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootApplication
@Import(value={H2ConsoleAutoConfiguration.class})
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
