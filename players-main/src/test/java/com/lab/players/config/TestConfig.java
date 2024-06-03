package com.lab.players.config;

import com.lab.players.file.whatcher.PlayersFileWatcher;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

;

@TestConfiguration
@TestPropertySource(locations = "classpath:application-test.yaml")
@Profile("test")
public class TestConfig {

    @Bean
    @ConditionalOnProperty(name = "service.stub", havingValue = "true")
    public PlayersFileWatcher playersSymbolMappingFileWatcher(@Value("${file.monitoring.interval:5000}") final long monitoringInterval,
                                                              @Value("${players.config.source}") final File monitoringFileLocation,
                                                              FileAlterationListenerAdaptor listener) {
        return null;
    }

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

}
