package com.chirko.onLine.repo;

import com.chirko.onLine.entity.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepo extends CrudRepository<Comment, UUID> {
    @Query("""
            SELECT c
            FROM Comment c
            JOIN FETCH c.user.images
            WHERE c.id = (:id)
            """)
    Comment findByIdFetchUserImages(@Param("id") UUID id);
}
