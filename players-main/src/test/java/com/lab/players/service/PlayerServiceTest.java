package com.lab.players.service;

import com.lab.players.entities.Player;
import com.lab.players.mapping.PlayerMapper;
import com.lab.players.repositories.PlayersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@SpringBootTest(properties = "service.stub=true")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @MockBean
    private PlayersRepository playersRepository;

    private final PlayerMapper playerMapper = new PlayerMapper();

    private final String player1 = "aberal01,1927,7,31,USA,OH,Cleveland,1993,5,20,USA,OH,Garfield Heights,Al,Aber,Albert Julius,195,74,L,L,1950-09-15,1957-09-11,abera101,aberal01";
    private final String player2 = "abercda01,1850,1,2,USA,OK,Fort Towson,1939,11,11,USA,PA,Philadelphia,Frank,Abercrombie,Francis Patterson,,,,,1871-10-21,1871-10-21,aberd101,abercda01";
    private final String player3 = "abernte01,1921,10,30,USA,NC,Bynum,2001,11,16,USA,NC,Charlotte,Ted,Abernathy,Talmadge Lafayette,210,74,R,L,1942-09-19,1944-04-29,abert102,abernte01";

    @Test
    void testCacheForFindAll() {
        // Mock the behavior of the repository
        List<Player> players = new ArrayList<>();
        players.add(playerMapper.apply(player1));
        players.add(playerMapper.apply(player2));

        when(playersRepository.findAll()).thenReturn(players);

        IntStream.range(0, 25).forEach(i -> playerService.findAll());

        // Verify that the repository method is called only once
        verify(playersRepository, times(1)).findAll();
    }

    @Test
    void testCacheForFindById() {
        // Mock the behavior of the repository
        Player player = playerMapper.apply(player1);
        when(playersRepository.findById("1")).thenReturn(Optional.of(player));

        IntStream.range(0, 25).forEach(i -> playerService.findById("1"));

        // Verify that the repository method is called only once
        verify(playersRepository, times(1)).findById("1");
    }

    @Test
    void testInvalidateCacheForFindAll() {
        // Mock the behavior of the repository
        List<Player> players = new ArrayList<>();
        players.add(playerMapper.apply(player1));
        players.add(playerMapper.apply(player2));

        when(playersRepository.findAll()).thenReturn(players);

        IntStream.range(0, 25).forEach(i -> playerService.findAll());

        // Invalidate the cache
        players.add(playerMapper.apply(player3));

        playerService.saveAll(players);

        IntStream.range(0, 25).forEach(i -> playerService.findAll());

        // Verify that the repository method is called only once
        verify(playersRepository, times(2)).findAll();
    }

    @Test
    void testInvalidateCacheForFindById() {
        // Mock the behavior of the repository
        Player player = playerMapper.apply(player1);
        List<Player> players = new ArrayList<>();
        players.add(playerMapper.apply(player2));
        players.add(playerMapper.apply(player3));

        when(playersRepository.findById("1")).thenReturn(Optional.of(player));

        IntStream.range(0, 25).forEach(i -> playerService.findById("1"));

        playerService.saveAll(players);

        IntStream.range(0, 25).forEach(i -> playerService.findById("1"));

        // Verify that the repository method is called only once
        verify(playersRepository, times(2)).findById("1");
    }
}
