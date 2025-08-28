package com.zandome.syncplaylist.user.infra.repositories;

import com.zandome.syncplaylist.user.domain.model.entities.User;
import com.zandome.syncplaylist.user.domain.ports.repositories.UserRepository;
import com.zandome.syncplaylist.user.infra.persistence.mongo.entities.UserDocument;
import com.zandome.syncplaylist.user.infra.persistence.mongo.mappers.UserMongoMapper;
import com.zandome.syncplaylist.user.infra.persistence.mongo.repositories.UserMongoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMongoRepository mongoRepository;
    private final UserMongoMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserDocument document = userMapper.toDocument(user);
        UserDocument savedDocument = mongoRepository.save(document);
        return userMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }
}