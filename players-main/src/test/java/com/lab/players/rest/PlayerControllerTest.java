package com.lab.players.rest;

import com.lab.players.entities.Player;
import com.lab.players.file.whatcher.PlayersFileProcessorAware;
import com.lab.players.service.PlayerServiceAware;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(properties = "service.stub=true")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
public class PlayerControllerTest {

    @Autowired
    private PlayerServiceAware<Player, String> serviceAware;

    @Autowired
    private WebTestClient webTestClient;


    @Autowired
    private PlayersFileProcessorAware playersFileProcessorAware;

    private File testFile;

    @BeforeEach
    public void setUp() throws IOException {

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("players-test.csv")) {
            testFile = Files.createTempFile("test_file", ".txt").toFile();
            // Copy the contents of the resource file to the temporary file
            FileCopyUtils.copy(inputStream, Files.newOutputStream(testFile.toPath()));
        }
        this.playersFileProcessorAware.onFileChange(testFile);
    }

    @Test
    public void testGetPlayers() throws Exception {

        webTestClient.get().uri("/api/players/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Player.class)
                .consumeWith(response -> {
                    List<Player> players = response.getResponseBody();
                    // Assert that players are returned from the database
                    assertNotNull(players);
                    assertEquals(59, players.size());
                });
    }

    @Test
    public void testGetById() throws Exception {

        webTestClient.get().uri("/api/players/abbotpa01")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Player.class)
                .consumeWith(response -> {
                    Player player = response.getResponseBody();
                    // Assert that players are returned from the database
                    assertNotNull(player);
                });
    }

    @Test
    public void testGetPageable() throws Exception {

        // Perform GET request with pageable and sort parameters
        webTestClient.get().uri("/api/players?page=0&size=10&sort=playerID,desc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String playersPage = response.getResponseBody();
                    // Assert that players are returned from the database
                    assertNotNull(playersPage);
;                   log.info("Players page: {}", playersPage);
                });
    }
}
