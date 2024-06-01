package com.lab.players.file.whatcher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"service.stub=true", "spring.h2.console.enabled=false"})
@TestPropertySource(properties = {
        "file.min.chunks.size=1048576" // 1 MB for test
})
@Slf4j
class PlayersFileSplitsTest {

    @Autowired
    private Function<Path, List<long[]>> playersSplits;

    @Value("${file.min.chunks.size: 1048576}")
    private long minChunkSize;

    @Test
    public void testProduceSplits() throws Exception {
        // Create a temporary file with a specific size
        Path tempFile = Files.createTempFile("testfile", ".tmp");
        Files.write(tempFile, new byte[(int) (5 * minChunkSize + 500000)]);

        List<long[]> splits = playersSplits.apply(tempFile);
        log.info("Splits six: {}", splits);


        assertNotNull(splits);
        assertEquals(6, splits.size()); // Expecting 6 splits

        assertArrayEquals(new long[]{0, minChunkSize}, splits.get(0));
        assertArrayEquals(new long[]{minChunkSize, 2 * minChunkSize}, splits.get(1));
        assertArrayEquals(new long[]{2 * minChunkSize, 3 * minChunkSize}, splits.get(2));
        assertArrayEquals(new long[]{3 * minChunkSize, 4 * minChunkSize}, splits.get(3));
        assertArrayEquals(new long[]{4 * minChunkSize, 5 * minChunkSize}, splits.get(4));
        assertArrayEquals(new long[]{5 * minChunkSize, 5 * minChunkSize + 500000}, splits.get(5));

        // Clean up the temporary file
        Files.deleteIfExists(tempFile);
    }


    @Test
    public void testEmptyFile() throws Exception {
        Path tempFile = Files.createTempFile("emptyfile", ".tmp");

        List<long[]> splits = playersSplits.apply(tempFile);
        log.info("Splits empty: {}", splits);

        assertNotNull(splits);
        assertTrue(splits.isEmpty()); // Expecting no splits for an empty file

        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testSmallFile() throws Exception {
        Path tempFile = Files.createTempFile("smallfile", ".tmp");
        Files.write(tempFile, new byte[(int) (minChunkSize / 2)]);

        List<long[]> splits = playersSplits.apply(tempFile);
        log.info("Splits one: {}", splits);

        assertNotNull(splits);
        assertEquals(1, splits.size()); // Expecting 1 split

        assertArrayEquals(new long[]{0, minChunkSize / 2}, splits.get(0));

        Files.deleteIfExists(tempFile);
    }
}