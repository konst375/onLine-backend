package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Friendship;
import com.chirko.onLine.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FriendshipRepo extends CrudRepository<Friendship, UUID> {
    Optional<Friendship> findByRecipientAndSender(User recipient, User sender);

    @Query("""
            SELECT COUNT(f) > 0
            FROM Friendship f
            WHERE f.recipient IN :users
            AND f.sender IN :users
            """)
    boolean existsByUsers(Set<User> users);

    @Query("""
            SELECT f
            FROM Friendship f
            WHERE f.recipient IN :users
            AND f.sender IN :users
            """)
    Optional<Friendship> findByUsers(Set<User> users);

    @Query("""
            SELECT f
            FROM Friendship f
            WHERE f.sender = :user
            OR f.recipient = :user
            """)
    Optional<Set<Friendship>> findByUser(@Param("user") User user);
}
