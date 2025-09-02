package com.zandome.syncplaylist.user.infra.repositories;

import com.zandome.syncplaylist.user.domain.model.entities.UserAuthentication;
import com.zandome.syncplaylist.user.domain.model.enums.AuthProvider;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserAuthenticationRepository;
import com.zandome.syncplaylist.user.infra.persistence.mongo.mappers.UserAuthenticationMongoMapper;
import com.zandome.syncplaylist.user.infra.persistence.mongo.repositories.UserAuthenticationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAuthenticationRepositoryImpl implements UserAuthenticationRepository {
    
    private final UserAuthenticationMongoRepository mongoRepository;
    private final UserAuthenticationMongoMapper mapper;

    @Override
    public UserAuthentication save(UserAuthentication userAuthentication) {
        var document = mapper.toDocument(userAuthentication);
        var saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserAuthentication> findByEmailAndProvider(String email, AuthProvider provider) {
        return mongoRepository.findByEmailAndProvider(email, provider)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserAuthentication> findByProviderIdAndProvider(String providerId, AuthProvider provider) {
        return mongoRepository.findByProviderIdAndProvider(providerId, provider)
                .map(mapper::toDomain);
    }

    @Override
    public List<UserAuthentication> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserAuthentication> findActiveByUserId(String userId) {
        return mongoRepository.findByUserIdAndIsActive(userId, true)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmailAndProvider(String email, AuthProvider provider) {
        return mongoRepository.existsByEmailAndProvider(email, provider);
    }

    @Override
    public boolean existsByProviderIdAndProvider(String providerId, AuthProvider provider) {
        return mongoRepository.existsByProviderIdAndProvider(providerId, provider);
    }
}