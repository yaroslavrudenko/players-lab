package com.lab.players.service;

import com.lab.players.entities.Player;
import com.lab.players.repositories.PlayersRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class PlayerService implements PlayerServiceAware<Player, String> {

    @NonNull
    private final PlayersRepository playersRepository;

    @Override
    @Transactional
    public List<Player> saveAll(List<Player> players) {
        return this.playersRepository.saveAll(players);
    }

    @Override
    public List<Player> findAll() {
        return this.playersRepository.findAll();
    }

    @Override
    public Optional<Player> findById(String s) {
        return this.playersRepository.findById(s);
    }
}
