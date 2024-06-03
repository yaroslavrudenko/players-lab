package com.lab.players.config;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayersMetricsConfig {

    @Bean("updatedPlayersCounter")
    public Counter getUpdatedPlayersCounter(MeterRegistry meterRegistry) {
        return Counter
                .builder("updated_players_total")
                .description("Total amount of updated players.")
                .register(meterRegistry);
    }
}