package com.chirko.onLine.user.repo;

import com.chirko.onLine.user.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
