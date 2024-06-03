package com.lab.players.file.process;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"service.stub=true", "spring.h2.console.enabled=false"})
@TestPropertySource(properties = {
        "file.min.chunks.size=1024"
})
@Slf4j
class PlayersFileIteratorProviderTest {

    @Autowired
    private Function<Path, List<long[]>> playersSplits;


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
    public void testFileIterator() {
        try (PlayersFilterIteratorAware playersRecordIteratorProvider = new PlayersRecordIteratorProvider(testFile)) {
            List<long[]> splits = this.playersSplits.apply(testFile.toPath());
            log.info("Splits: {}", splits);

            assertEquals(8, splits.size());

            int count = 0;
            for (int i = 0; i < splits.size(); i++) {
                long[] split = splits.get(i);

                assertNotNull(split);
                assertEquals(2, split.length);

                Iterator<String> iterator = playersRecordIteratorProvider.getRecordIterator(split[0], split[1], TRUE);
                assertTrue(iterator.hasNext());

                while (iterator.hasNext()) {
                    String record = iterator.next();
                    assertNotNull(record);

                    ++count;
                    log.info("[{}-{}] Record: {}", i, count, record);
                }
            }
            assertEquals(59, count, "Expected 60 records from file with header");
        } catch (Exception e) {
            fail("Error getting record iterator", e);
        }
    }
}