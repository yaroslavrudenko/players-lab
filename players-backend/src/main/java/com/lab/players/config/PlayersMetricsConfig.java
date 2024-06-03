package com.lab.players.config;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean("popularPlayersCounters")
    public Cache<String, Counter> getPopularPlayersCounters(@Value("${players.cache.size:1024}") int cacheSize) {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build();
    }
}