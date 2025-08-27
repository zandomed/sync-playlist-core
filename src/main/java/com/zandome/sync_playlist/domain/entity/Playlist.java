package com.zandome.sync_playlist.domain.entity;

import java.util.List;

import com.zandome.sync_playlist.domain.vo.PlaylistImage;

public class Playlist {
    String id;
    String name;
    String description;
    List<PlaylistImage> images;
    boolean collaborative;
    List<Track> tracks;
}
