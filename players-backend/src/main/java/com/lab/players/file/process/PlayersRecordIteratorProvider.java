package com.lab.players.file.process;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.*;
import java.nio.channels.Channels;
import java.util.Iterator;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * PlayersRecordIteratorProvider provides a record iterator for the given byte offsets.
 *
 * @author Yaroslav Rudenko
 * @see PlayersFilterIteratorAware
 * @see Iterator
 * @see RandomAccessFile
 * @see InputStream
 * @see IOUtils
 * @see BoundedInputStream
 */
@Slf4j
public class PlayersRecordIteratorProvider implements PlayersFilterIteratorAware {

    private final File file;
    private RandomAccessFile randomAccessFile;
    private InputStream inputStream;

    public PlayersRecordIteratorProvider(File file) {
        this.file = file;
    }

    /**
     * Method to create an iterator containing the records in a portion of a file.
     * The iterator streams records from file as they are read.  Reads from the
     * beginning of the first full record in the fileSplit until the end of the
     * last record that begins in the fileSplit
     * ie reads from startByteOffset + X to endByteOffset + Y
     *
     * @param startByteOffset Offset from the start of the file to start reading from
     * @param endByteOffset   Offset from the start of the file to read until
     * @param skipHeader      Whether to skip the first line of the file (this is a HACK for the header line in the file)
     * @return Iterator containing records in split
     */
    @Override
    public Iterator<String> getRecordIterator(long startByteOffset, long endByteOffset, boolean skipHeader /* HACK */) throws IOException {

        startByteOffset = startByteOffset == 0 && skipHeader ? startByteOffset + 1 : startByteOffset;
        long lastRecordOffset = this.getNextRecordOffset(this.file, endByteOffset);

        this.randomAccessFile = new RandomAccessFile(this.file, "r");
        this.randomAccessFile.seek(startByteOffset);

        long streamFileSplitSize = endByteOffset - startByteOffset + lastRecordOffset;

        this.inputStream = new BufferedInputStream(
                BoundedInputStream.builder()
                        .setInputStream(Channels.newInputStream(this.randomAccessFile.getChannel()))
                        .setMaxCount(streamFileSplitSize)
                        .get());
        Iterator<String> lineIterator = IOUtils.lineIterator(this.inputStream, UTF_8);
        // Skip the first partial line if not starting from the beginning
        if (startByteOffset != 0 && lineIterator.hasNext()) {
            lineIterator.next();
        }
        return lineIterator;
    }


    private long getNextRecordOffset(File file, long byteOffset) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             InputStream inputStream = new BufferedInputStream(Channels.newInputStream(raf.getChannel()))) {
            raf.seek(byteOffset);

            Iterator<String> tmpIterator = IOUtils.lineIterator(inputStream, UTF_8);
            if (tmpIterator.hasNext()) {
                return tmpIterator.next().length();
            } else {
                return 0;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting next record offset for file: " + file, e);
        }
    }

    @Override
    public void close() throws IOException {
        log.info("Closing InputStream for file: {}", this.file.getName());
        IOUtils.closeQuietly(inputStream);
        log.info("Closing RandomAccessFile for file: {}", this.file.getName());
        IOUtils.closeQuietly(randomAccessFile);
    }
}
