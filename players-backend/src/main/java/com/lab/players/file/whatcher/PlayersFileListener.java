package com.lab.players.file.whatcher;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class PlayersFileListener extends FileAlterationListenerAdaptor implements PlayersFileListenerAware {

    @NonNull
    private Function<Path, List<long[]>> playersSplits;

    @NonNull
    private PlayersSplitProcessorAware playersSplitProcessor;

    @Override
    public void onFileChange(File file) {
        log.info("Changes detected! Players file {} has been changed !", file);
        List<long[]> splits = playersSplits.apply(file.toPath());
        log.info("Detected Splits: {}", splits.size());

        // TODO: Think about processPlayers method with parallelStream or @Async. DO we need wait for all chunks to be processed?
        splits.parallelStream().forEach(split -> this.playersSplitProcessor.processPlayers(file, split));
        log.info("All players have been processed for file: {} with chunks amount: {}", file.getName(), splits.size());
    }
}
