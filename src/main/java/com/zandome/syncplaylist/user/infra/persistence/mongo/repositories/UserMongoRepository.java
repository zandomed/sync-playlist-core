package com.zandome.syncplaylist.user.infra.persistence.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zandome.syncplaylist.user.infra.persistence.mongo.entities.UserDocument;

import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);

    boolean existsByEmail(String email);
}