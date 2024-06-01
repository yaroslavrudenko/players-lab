package com.lab.players.config;

import com.lab.players.file.whatcher.PlayersFileWatcher;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.File;

@TestConfiguration
public class TestConfig {

    @Bean
    @ConditionalOnProperty(name="service.stub", havingValue="true")
    public PlayersFileWatcher playersSymbolMappingFileWatcher(@Value("${file.monitoring.interval:5000}") final long monitoringInterval,
                                                              @Value("${players.config.source}") final File monitoringFileLocation,
                                                              FileAlterationListenerAdaptor listener) {
        return null;
    }

}
