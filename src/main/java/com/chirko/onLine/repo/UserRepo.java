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
            JOIN FETCH u.images
            WHERE u.id = (SELECT p.user.id FROM Post p WHERE p.id = :postId)
            """)
    Optional<User> findUserByPostIdAndFetchAvatarEagerly(UUID postId);
    @Query("""
            SELECT u
            FROM User u
            JOIN FETCH u.images
            JOIN FETCH u.posts
            WHERE u.id = :userId
            """)
    Optional<User> findUserByIdAndFetchPostsAndImagesEagerly(UUID userId);// TODO: 27.03.2023 order post and images by createdDate
}
