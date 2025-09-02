package com.zandome.syncplaylist.user.infra.persistence.mongo.entities;

import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_authentications")
@CompoundIndexes({
        @CompoundIndex(def = "{'email': 1, 'provider': 1}", unique = true),
        @CompoundIndex(def = "{'providerId': 1, 'provider': 1}", unique = true),
        @CompoundIndex(def = "{'userId': 1}")
})
public class UserAuthenticationDocument {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private AuthProvider provider;

    @Field("provider_id")
    private String providerId;

    private String email;

    @Field("password_hash")
    private String passwordHash;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("last_used_at")
    private LocalDateTime lastUsedAt;

    @Field("is_active")
    private boolean isActive;
}