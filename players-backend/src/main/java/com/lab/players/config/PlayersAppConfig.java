package com.lab.players.config;

import com.lab.players.file.whatcher.PlayersFileWatcher;
import lombok.extern.slf4j.Slf4j;
import me.yaman.can.webflux.h2console.H2ConsoleAutoConfiguration;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.io.File;

@Slf4j
@Configuration
@EnableScheduling
@EnableAsync
@EnableWebFlux
@EnableJpaRepositories(basePackages = {"com.lab.players.repositories"})
@EntityScan("com.lab.players.entities")
@Import(value = {H2ConsoleAutoConfiguration.class})
public class PlayersAppConfig {

    @Bean
    @ConditionalOnProperty(name = "service.stub", havingValue = "false", matchIfMissing = true)
    public PlayersFileWatcher playersSymbolMappingFileWatcher(@Value("${file.monitoring.interval:5000}") final long monitoringInterval,
                                                              @Value("${players.config.source}") final File monitoringFileLocation,
                                                              FileAlterationListenerAdaptor listener) {
        log.info("Creating PlayersSymbolMappingFileWatcher");
        return new PlayersFileWatcher(monitoringInterval, monitoringFileLocation, listener);
    }
}
