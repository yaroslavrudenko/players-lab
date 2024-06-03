package com.lab.players.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class PlayerMetricsService implements BiConsumer<String, String> {

    private Map<String, Counter> counters;

    @NonNull
    private MeterRegistry meterRegistry;

    @PostConstruct
    public void init() {
        this.counters = new ConcurrentHashMap<>();
    }

    @PreDestroy
    public void destroy() {
        this.counters.clear();
    }

    @Override
    public void accept(final String countableName, final String countableValue) {
        this.counters.computeIfAbsent(countableValue,
                        key -> Counter
                                .builder("popular_players_total")
                                .description("Total amount of requests for popular players.")
                                .tag(countableName, countableValue)
                                .register(meterRegistry))
                .increment();
    }
}
