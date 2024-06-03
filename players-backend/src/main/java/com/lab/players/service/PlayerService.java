package com.lab.players.service;

import com.lab.players.entities.Player;
import com.lab.players.repositories.PlayersRepository;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.lab.players.config.PlayersCacheConfig.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class PlayerService implements PlayerServiceAware<Player, String> {

    @NonNull
    private final PlayersRepository playersRepository;

    @NonNull
    private final Counter updatedPlayersCounter;

    @NonNull
    private final BiConsumer<String, String> playerMetricsService;

    @Override
    @Transactional
    @Counted(value = "players_save_all_errors", recordFailuresOnly = true, extraTags = {"service"})
    @Timed(value = "players_save_all_latency", description = "Players SaveAll latency", extraTags = {"service"})
    @CacheEvict(value = {SERVICE_FIND_ALL, SERVICE_FIND_BY_ID, SERVICE_FIND_ALL_PAGEABLE}, allEntries = true)
    public List<Player> saveAll(List<Player> players) {
        List<Player> updatedPlayers = this.playersRepository.saveAll(players);
        this.updatedPlayersCounter.increment(updatedPlayers.size());
        return updatedPlayers;
    }

    @Override
    @Counted(value = "players_find_all_errors", recordFailuresOnly = true, extraTags = {"service"})
    @Timed(value = "players_find_all_latency", description = "Players FindAll latency", extraTags = {"service"})
    @Cacheable(value = SERVICE_FIND_ALL, unless = "#result instanceof T(java.lang.Exception)", cacheManager = "cacheManager",
            keyGenerator = "workingKeyGenerator")
    public List<Player> findAll() {
        List<Player> allPlayers = this.playersRepository.findAll();
        log.info("Found in database {} players", allPlayers.size());
        return allPlayers;
    }

    @Override
    @Counted(value = "players_find_by_id_errors", recordFailuresOnly = true, extraTags = {"service"})
    @Timed(value = "players_find_by_id_latency", description = "Players FindById latency", extraTags = {"service"})
    @Cacheable(value = SERVICE_FIND_BY_ID, unless = "#result instanceof T(java.lang.Exception)", cacheManager = "cacheManager",
            keyGenerator = "workingKeyGenerator")
    public Optional<Player> findById(String id) {
        Optional<Player> player = this.playersRepository.findById(id);
        this.playerMetricsService.accept("playerID", id);
        log.info("Tried to find in database player with id {}, player found: {}", id, player.isPresent());
        return player;
    }

    @Override
    @Counted(value = "players_find_all_errors", recordFailuresOnly = true, extraTags = {"pageable"})
    @Timed(value = "players_find_all_latency", description = "Players FindAll latency", extraTags = {"pageable"})
    @Cacheable(value = SERVICE_FIND_ALL_PAGEABLE, unless = "#result instanceof T(java.lang.Exception)", cacheManager = "cacheManager",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<Player> findAll(@org.springframework.lang.NonNull Pageable pageable) {
        Page<Player> page = this.playersRepository.findAll(pageable);
        log.info("Found in database {} players for offset {}, page number: {}, page size: {}, sort: {}",
                page.getTotalElements(), pageable.getOffset(), pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return page;
    }
}
