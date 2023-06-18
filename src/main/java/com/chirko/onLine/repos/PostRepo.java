package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepo extends CrudRepository<Post, UUID> {
    @Query("""
            SELECT p
            FROM Post p
            WHERE p.id = :id
            """)
    @EntityGraph(attributePaths = {"tags", "images"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Post> findByIdWithTagsAndImages(@Param("id") UUID id);
}
