package com.chirko.onLine.repo;

import com.chirko.onLine.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    @Query("""
            SELECT u
            FROM User u
            JOIN FETCH u.imagesList
            WHERE u.id = (SELECT p.user.id FROM Post p WHERE p.id = :postId)
            """)
    Optional<User> findUserAndFetchAvatarEagerlyByPostId(UUID postId);
}
