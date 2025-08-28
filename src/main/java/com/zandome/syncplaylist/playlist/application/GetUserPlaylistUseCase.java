package com.zandome.syncplaylist.playlist.application;

import java.util.List;

import com.zandome.syncplaylist.playlist.domain.entities.Playlist;
import com.zandome.syncplaylist.shared.application.interfaces.UseCase;

public class GetUserPlaylistUseCase implements UseCase<String, List<Playlist>> {

    @Override
    public List<Playlist> execute(String userId) {

        return List.of();
        // Implementation goes here
    }
}
