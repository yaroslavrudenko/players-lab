package com.lab.players.file.whatcher;

import java.io.File;

public interface PlayersFileListenerAware {
    void onFileChange(File file);
}
