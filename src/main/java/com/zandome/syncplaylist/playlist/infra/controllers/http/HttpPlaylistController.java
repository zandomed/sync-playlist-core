package com.zandome.syncplaylist.playlist.infra.controllers.http;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zandome.syncplaylist.playlist.domain.entities.Playlist;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/playlists")
public class HttpPlaylistController {

    @GetMapping("")
    public List<Playlist> getAllPlaylists() {
        // Implementation goes here
        return List.of();
    }

}
