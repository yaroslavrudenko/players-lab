package com.lab.players.rest;

import com.lab.players.entities.Player;
import com.lab.players.service.PlayerServiceAware;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class PlayersRestController {

    @NonNull
    private PlayerServiceAware<Player, String> playerService;

    @GetMapping(produces = "application/stream+json")
    public Flux<Player> getAllPlayers() {
        return Flux.fromIterable(playerService.findAll());
    }

    @GetMapping(value = "/{playerID}", produces = "application/stream+json")
    public Mono<Player> getPlayerById(@PathVariable String playerID) {
        return playerService.findById(playerID).map(Mono::just)
                .orElseGet(Mono::empty);
    }
}
