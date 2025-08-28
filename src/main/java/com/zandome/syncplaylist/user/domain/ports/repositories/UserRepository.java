package com.zandome.syncplaylist.user.domain.ports.repositories;

import java.util.Optional;

import com.zandome.syncplaylist.user.domain.model.entities.User;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(String id);

    boolean existsByEmail(String email);
}