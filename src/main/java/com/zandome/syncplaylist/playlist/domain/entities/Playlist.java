package com.zandome.syncplaylist.playlist.domain.entities;

import java.util.List;

import com.zandome.syncplaylist.playlist.domain.vo.PlaylistImage;

import lombok.Builder;

@Builder
public class Playlist {
    String id;
    String name;
    String description;
    List<PlaylistImage> images;
    boolean collaborative;
    List<Track> tracks;
}
