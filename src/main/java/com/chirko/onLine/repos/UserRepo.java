package com.chirko.onLine.repos;

import com.chirko.onLine.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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
            WHERE u = :user
            """)
    @EntityGraph(attributePaths = {"images"})
    Optional<User> findUserAndFetchImagesEagerly(@Param("user") User user);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            """)
    @EntityGraph(attributePaths = {"images", "posts"})
    Optional<User> findByIdAndFetchUserImagesAndPostsEagerly(@Param("id") UUID id);
}
