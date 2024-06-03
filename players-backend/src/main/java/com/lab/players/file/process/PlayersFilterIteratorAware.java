package com.lab.players.file.process;

import java.io.IOException;
import java.util.Iterator;

public interface PlayersFilterIteratorAware extends AutoCloseable {
    Iterator<String> getRecordIterator(long startByteOffset, long endByteOffset, boolean skipHeader) throws IOException;
}
