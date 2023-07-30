package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Like;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepo extends CrudRepository<Like, UUID> {
    @Modifying
    @Query("""
            DELETE
            FROM Like l
            WHERE (l.user.id = :userId AND l.img.id = :id)
            OR (l.user.id = :userId AND l.post.id = :id)
            OR (l.user.id = :userId AND l.comment.id = :id)
            """)
    void deleteByUserIdAndParentId(@Param("id") UUID id, @Param("userId") UUID userId);

    boolean existsByUserIdAndPostId(UUID userId, UUID postId);

    boolean existsByUserIdAndImgId(UUID userId, UUID imgId);

    boolean existsByUserIdAndCommentId(UUID userId, UUID imgId);

    @Query("""
            SELECT l
            FROM Like l
            WHERE (l.user.id = :userId AND l.img.id = :id)
            OR (l.user.id = :userId AND l.post.id = :id)
            OR (l.user.id = :userId AND l.comment.id = :id)
            """)
    Optional<Like> findByUserIdParentId(@Param("id") UUID id, @Param("userId") UUID userId);
}
