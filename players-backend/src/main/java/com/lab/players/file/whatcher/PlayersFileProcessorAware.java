package com.lab.players.file.whatcher;

import com.lab.players.entities.Player;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public interface PlayersFileProcessorAware {

    void onFileChange(File file);

    void processPlayers(File file, long[] split);

    List<Player> createPlayers(Iterator<String> iterator);
}
