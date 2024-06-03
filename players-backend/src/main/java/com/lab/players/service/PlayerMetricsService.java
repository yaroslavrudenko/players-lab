package com.lab.players.service;

import com.google.common.cache.Cache;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class PlayerMetricsService implements BiConsumer<String, String> {

    @NonNull
    private Cache<String, Counter> popularPlayersCounters;

    @NonNull
    private MeterRegistry meterRegistry;

    @Override
    public void accept(final String countableName, final String countableValue) {
        try {
            this.popularPlayersCounters.get(countableValue, () -> Counter
                            .builder("popular_players_total")
                            .description("Total amount of requests for popular players.")
                            .tag(countableName, countableValue)
                            .register(meterRegistry))
                    .increment();
        } catch (ExecutionException e) {
            log.error("Can't increment counter for player {}", countableValue, e);
        }
    }
}
