package com.lab.players.file.whatcher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "service.stub=true")
@TestPropertySource(properties = {
        "file.min.chunks.size=16"
})
@Slf4j
class PlayersRecordIteratorProviderTest {

    @Autowired
    private Function<Path, List<long[]>> playersSplits;

    private File testFile;

    /**
     * line1 -> Byte Offset: 0
     * line2 -> Byte Offset: 7
     * line3 -> Byte Offset: 13
     * line4 -> Byte Offset: 19
     * line5 -> Byte Offset: 25
     */
    @BeforeEach
    public void setUp() throws IOException {
        testFile = File.createTempFile("test", ".csv");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("line1\nline2\nline3\nline4\nline5");
        }
    }

    @Test
    public void testGetRecordIterator() {

        try (PlayersFilterIteratorAware playersRecordIteratorProvider = new PlayersRecordIteratorProvider(testFile)) {
            List<long[]> splits = this.playersSplits.apply(testFile.toPath());
            log.info("Splits: {}", splits);

            assertEquals(2, splits.size());

            long[] split = splits.get(0);
            Iterator<String> iterator = playersRecordIteratorProvider.getRecordIterator(split[0], split[1], FALSE);

            assertTrue(iterator.hasNext());
            assertEquals("line1", iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals("line2", iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals("line3", iterator.next());
            assertFalse(iterator.hasNext());

            split = splits.get(1);
            iterator = playersRecordIteratorProvider.getRecordIterator(split[0], split[1], TRUE);
            assertTrue(iterator.hasNext());
            assertEquals("line4", iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals("line5", iterator.next());
            assertFalse(iterator.hasNext());

        } catch (Exception e) {
            fail("Error getting record iterator", e);
        }
    }

    @Test
    public void testGetSimpleRecordIterator() {
        long startByteOffset = 0;
        long endByteOffset = 12; // Should cover "line1\nline2\n"

        try (PlayersFilterIteratorAware playersRecordIteratorProvider = new PlayersRecordIteratorProvider(testFile)) {
            Iterator<String> iterator = playersRecordIteratorProvider.getRecordIterator(startByteOffset, endByteOffset, FALSE);

            assertTrue(iterator.hasNext());
            assertEquals("line1", iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals("line2", iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals("line3", iterator.next());
            assertFalse(iterator.hasNext());
        } catch (Exception e) {
            fail("Error getting record iterator", e);
        }
    }

    @Test
    public void testGetRecordIteratorWithOffset() {
        long startByteOffset = 6; // Starts mid "line1\nline2\n"
        long endByteOffset = 18;  // Should cover "line2\nline3\nline4\n"

        try (PlayersFilterIteratorAware playersRecordIteratorProvider = new PlayersRecordIteratorProvider(testFile)) {
            Iterator<String> iterator = playersRecordIteratorProvider.getRecordIterator(startByteOffset, endByteOffset, FALSE);

            assertTrue(iterator.hasNext());
            assertEquals("line3", iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals("line4", iterator.next());
            assertFalse(iterator.hasNext());

        } catch (Exception e) {
            fail("Error getting record iterator", e);
        }
    }
}