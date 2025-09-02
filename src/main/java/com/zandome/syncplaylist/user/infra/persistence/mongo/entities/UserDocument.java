package com.zandome.syncplaylist.user.infra.persistence.mongo.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@Document(collection = "users")
public class UserDocument {
    @Id
    private String id;

    private String name;
    @Field("last_name")

    private String lastName;

    @Indexed(unique = true)
    private String email;

    @Field("profile_picture_url")
    private String profilePictureUrl;
}