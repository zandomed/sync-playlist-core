package com.zandome.sync_playlist.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
    String id;
    String email;
    String name;
    String lastName;
    String password;
}
