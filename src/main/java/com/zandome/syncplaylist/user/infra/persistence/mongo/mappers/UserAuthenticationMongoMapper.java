package com.zandome.syncplaylist.user.infra.persistence.mongo.mappers;

import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.infra.persistence.mongo.entities.UserAuthenticationDocument;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationMongoMapper {
    
    public UserAuthenticationDocument toDocument(UserAuthentication userAuth) {
        return UserAuthenticationDocument.builder()
                .id(userAuth.getId())
                .userId(userAuth.getUserId())
                .provider(userAuth.getProvider())
                .providerId(userAuth.getProviderId())
                .email(userAuth.getEmail())
                .passwordHash(userAuth.getPasswordHash())
                .createdAt(userAuth.getCreatedAt())
                .lastUsedAt(userAuth.getLastUsedAt())
                .isActive(userAuth.isActive())
                .build();
    }
    
    public UserAuthentication toDomain(UserAuthenticationDocument document) {
        return UserAuthentication.builder()
                .id(document.getId())
                .userId(document.getUserId())
                .provider(document.getProvider())
                .providerId(document.getProviderId())
                .email(document.getEmail())
                .passwordHash(document.getPasswordHash())
                .createdAt(document.getCreatedAt())
                .lastUsedAt(document.getLastUsedAt())
                .isActive(document.isActive())
                .build();
    }
}