package com.chirko.onLine.repo;

import com.chirko.onLine.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends CrudRepository<User, UUID> {
    User findByEmail(String email);
}
