package com.zandome.syncplaylist.user.domain.model.entities;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {
    private final String id;
    private final String name;
    private final String lastName;
    private final String email;
    private final String profilePictureUrl;

    public String getFullName() {
        return name + " " + lastName;
    }
}