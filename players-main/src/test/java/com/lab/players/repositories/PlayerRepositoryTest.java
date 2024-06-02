package com.lab.players.repositories;

import com.lab.players.entities.Player;
import com.lab.players.file.whatcher.PlayersFileProcessorAware;
import com.lab.players.service.PlayerServiceAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"service.stub=true", "spring.h2.console.enabled=false"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yaml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PlayerRepositoryTest {

    @Autowired
    private PlayerServiceAware<Player, String> serviceAware;

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
    }

    @Test
    public void testFindAll() {
        this.playersFileProcessorAware.onFileChange(testFile);
        // Given: Test database is initialized with schema.sql
        // When: Retrieving all players from the repository
        Iterable<Player> players = serviceAware.findAll();

        // Hit a second time to test cache
        players = serviceAware.findAll();
        // Hit a third time to test cache
        players = serviceAware.findAll();
        // Hit a fourth time to test cache
        players = serviceAware.findAll();

        // Then: Verify that players are not empty
        assertThat(players).isNotEmpty();
        assertThat(players).hasSize(59);
    }

    @Test
    public void testPageable() {
        this.playersFileProcessorAware.onFileChange(testFile);
        // Given: Test database is initialized with schema.sql
        // When: Retrieving all players from the repository
        Iterable<Player> players = serviceAware.findAll(Pageable.ofSize(5).withPage(1));


        // Then: Verify that players are not empty
        assertThat(players).isNotEmpty();
        assertThat(players).hasSize(5);
    }

    /**
     * adairje01,1936,12,17,USA,OK,Sand Springs,1987,5,31,USA,OK,Tulsa,Jerry,Adair,Kenneth Jerry,175,72,R,R,1958-09-02,1970-05-03,adaij101,adairje01
     */
    @Test
    public void testGetById() {
        this.playersFileProcessorAware.onFileChange(testFile);
        // Given: Test database is initialized with schema.sql
        // When: Retrieving all players from the repository
        Optional<Player> player = serviceAware.findById("adairje01");

        // Hit a second time to test cache
        player = serviceAware.findById("adairje01");
        // Hit a third time to test cache
        player = serviceAware.findById("adairje01");
        // Hit a fourth time to test cache
        player = serviceAware.findById("adairje01");

        // Then: Verify that players are not empty
        assertThat(player).isPresent();
        assertThat(player.get().getPlayerID()).isEqualTo("adairje01");
        assertThat(player.get().getHeight()).isEqualTo(175);
        assertThat(player.get().getWeight()).isEqualTo(72);
        assertThat(player.get().getDeathYear()).isEqualTo(1987);
    }

}