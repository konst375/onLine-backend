package com.chirko.onLine.repos;

import com.chirko.onLine.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepo extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            """)
    @EntityGraph(attributePaths = "images")
    Optional<User> findByIdWithImages(@Param("id") UUID userId);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            """)
    @EntityGraph(attributePaths = {"comments", "communities", "viewedPosts"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByIdWithInterestIndicators(@Param("id") UUID userId);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            """)
    @EntityGraph(attributePaths = "viewedPosts", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findUserWithViewedPosts(@Param("id") UUID userId);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            """)
    @EntityGraph(attributePaths = "chats")
    Optional<User> findByIdWithChats(@Param("id") UUID userId);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.id IN :ids
            """)
    @EntityGraph(attributePaths = "images")
    Optional<Set<User>> findAllByIdWithImages(@Param("ids") Set<UUID> ids);
}
