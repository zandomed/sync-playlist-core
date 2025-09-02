package com.zandome.syncplaylist.user.infra.persistence.mongo.repositories;

import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.infra.persistence.mongo.entities.UserAuthenticationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAuthenticationMongoRepository extends MongoRepository<UserAuthenticationDocument, String> {
    Optional<UserAuthenticationDocument> findByEmailAndProvider(String email, AuthProvider provider);
    
    Optional<UserAuthenticationDocument> findByProviderIdAndProvider(String providerId, AuthProvider provider);
    
    List<UserAuthenticationDocument> findByUserId(String userId);
    
    List<UserAuthenticationDocument> findByUserIdAndIsActive(String userId, boolean isActive);
    
    boolean existsByEmailAndProvider(String email, AuthProvider provider);
    
    boolean existsByProviderIdAndProvider(String providerId, AuthProvider provider);
}