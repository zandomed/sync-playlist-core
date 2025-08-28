package com.zandome.syncplaylist.user.infra.persistence.mongo.mappers;

import com.zandome.syncplaylist.user.domain.model.entities.User;
import com.zandome.syncplaylist.user.infra.persistence.mongo.entities.UserDocument;

import org.springframework.stereotype.Component;

@Component
public class UserMongoMapper {

    public UserDocument toDocument(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public User toDomain(UserDocument document) {
        return User.builder()
                .id(document.getId())
                .name(document.getName())
                .lastName(document.getLastName())
                .email(document.getEmail())
                .password(document.getPassword())
                .build();
    }
}