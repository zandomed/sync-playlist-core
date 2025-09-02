package com.zandome.syncplaylist.user.domain.ports.repositories;

import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;

import java.util.List;
import java.util.Optional;

public interface UserAuthenticationRepository {
    UserAuthentication save(UserAuthentication userAuthentication);
    
    Optional<UserAuthentication> findByEmailAndProvider(String email, AuthProvider provider);
    
    Optional<UserAuthentication> findByProviderIdAndProvider(String providerId, AuthProvider provider);
    
    List<UserAuthentication> findByUserId(String userId);
    
    List<UserAuthentication> findActiveByUserId(String userId);
    
    void deleteById(String id);
    
    boolean existsByEmailAndProvider(String email, AuthProvider provider);
    
    boolean existsByProviderIdAndProvider(String providerId, AuthProvider provider);
}