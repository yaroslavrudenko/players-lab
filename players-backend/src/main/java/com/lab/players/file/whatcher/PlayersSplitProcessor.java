package com.lab.players.file.whatcher;

import com.lab.players.entities.Player;
import com.lab.players.file.process.PlayersFilterIteratorAware;
import com.lab.players.file.process.PlayersRecordIteratorProvider;
import com.lab.players.service.PlayerServiceAware;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
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
public class PlayersSplitProcessor implements PlayersSplitProcessorAware {

    public static final int START_BYTE = 0;
    public static final int END_BYTE = 1;

    @NonNull
    private PlayerServiceAware<Player, String> playerService;

    @NonNull
    private Function<String, Player> playerMapper;

    // TODO: Implement the processPlayers method with Async annotation. Think if this brings any benefits to the application.
    //@Async
    @Override
    public void processPlayers(File file, long[] split) {
        long startByte = split[START_BYTE];
        long endByte = split[END_BYTE];
        log.info("Start split bytes: start:{} - end:{} for file: {}", startByte, endByte, file.getName());
        try (PlayersFilterIteratorAware playersRecordIteratorProvider = new PlayersRecordIteratorProvider(file)) {
            Iterator<String> iterator = playersRecordIteratorProvider.getRecordIterator(startByte, endByte, TRUE);
            List<Player> players = this.createPlayers(iterator);
            this.playerService.saveAll(players);
        } catch (Exception e) {
            log.error("Error while reading file: {}. From byte: {}, to: {}", file, startByte, endByte, e);
        }
        log.info("Finish split bytes: start:{} - end:{} for file: {}", startByte, endByte, file.getName());
    }

    @Override
    public List<Player> createPlayers(Iterator<String> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, ORDERED), false)
                .map(this.playerMapper)
                .filter(Objects::nonNull)
                .collect(toList());
    }
}
