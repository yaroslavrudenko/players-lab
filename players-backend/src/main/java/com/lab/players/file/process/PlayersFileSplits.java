package com.lab.players.file.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class PlayersFileSplits implements Function<Path, List<long[]>> {

    @Value("${file.min.chunks.size: 1048576}") //1MB
    private long minChunkSize;

    @Override
    public List<long[]> apply(Path filePath) {
        long fileSize;
        try {
            fileSize = Files.size(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get file size.", e);
        }

        List<long[]> splits = new ArrayList<>();
        long start = 0;
        long remainingSize = fileSize;

        while (remainingSize > 0) {
            long splitSize = Math.min(this.minChunkSize, remainingSize);
            long end = start + splitSize;
            splits.add(new long[]{start, end});
            start = end;
            remainingSize -= splitSize;
        }

        return splits;
    }
}
