package com.lab.players.file.whatcher;


import com.lab.players.entities.Player;
import com.lab.players.service.PlayerServiceAware;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.lang.Boolean.TRUE;
import static java.util.Spliterator.ORDERED;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class PlayersFileProcessor extends FileAlterationListenerAdaptor implements PlayersFileProcessorAware {

    public static final int START_BYTE = 0;
    public static final int END_BYTE = 1;

    @NonNull
    private Function<Path, List<long[]>> playersSplits;

    @NonNull
    private PlayerServiceAware<Player, String> playerService;

    @NonNull
    private Function<String, Player> playerMapper;

    @Override
    public void onFileChange(File file) {
        log.info("Changes detected! Players file {} has been changed !", file);
        List<long[]> splits = playersSplits.apply(file.toPath());
        log.info("Detected Splits: {}", splits.size());

        splits.parallelStream().forEach(split -> this.processPlayers(file, split));
        log.info("All players have been processed for file: {} with chunks amount: {}", file.getName(), splits.size());
    }

    @Override
    public void processPlayers(File file, long[] split) {
        long startByte = split[START_BYTE];
        long endByte = split[END_BYTE];
        log.info("Split bytes: start:{} - end:{}", startByte, endByte);
        try (PlayersFilterIteratorAware playersRecordIteratorProvider = new PlayersRecordIteratorProvider(file)) {
            Iterator<String> iterator = playersRecordIteratorProvider.getRecordIterator(startByte, endByte, TRUE);
            List<Player> players = this.createPlayers(iterator);
            playerService.saveAll(players);
        } catch (Exception e) {
            log.error("Error while reading file: {}. From byte: {}, to: {}", file, startByte, endByte, e);
        }
    }

    @Override
    public List<Player> createPlayers(Iterator<String> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, ORDERED), false)
                .map(this.playerMapper)
                .filter(Objects::nonNull)
                .collect(toList());
    }

}
