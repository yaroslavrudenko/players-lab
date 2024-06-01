package com.lab.players.file.whatcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
//@Component
public class PlayersSymbolMappingFileWatcher {

    private final long monitoringInterval;
    private final String monitoringFileLocation;
    private final FileAlterationMonitor monitor;

    public PlayersSymbolMappingFileWatcher(@Value("${file.monitoring.interval}") final long monitoringInterval,
                                           @Value("${file.monitoring.path}") final File monitoringFileLocation,
                                           FileAlterationListenerAdaptor listener) {
        this.monitoringInterval = monitoringInterval;
        this.monitoringFileLocation = monitoringFileLocation.getAbsolutePath();
        FileAlterationObserver observer;
        if (monitoringFileLocation.isFile())
            observer = new FileAlterationObserver(monitoringFileLocation.getParentFile(), new NameFileFilter(monitoringFileLocation.getName()));
        else
            observer = new FileAlterationObserver(monitoringFileLocation);

        observer.addListener(listener);
        this.monitor = new FileAlterationMonitor(monitoringInterval, observer);
        listener.onFileChange(monitoringFileLocation);
        this.start();
    }

    private void start() {
        try {
            this.monitor.start();
            log.info("File monitoring started. File {}, interval {}", monitoringFileLocation, monitoringInterval);
        } catch (Exception e) {
            log.warn("Can't start symbols mapping monitoring ", e);
        }
    }

}
