package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Post;
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
public interface PostRepo extends CrudRepository<Post, UUID> {
    @Query("""
            SELECT p
            FROM Post p
            WHERE p.id = :id
            """)
    @EntityGraph(attributePaths = {"tags", "images", "likes", "comments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Post> findByIdWithAllDependencies(@Param("id") UUID id);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.user = :user
            """)
    @EntityGraph(attributePaths = {"tags", "images", "likes"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Set<Post>> findAllByAdminWithTagsImagesAndLikes(@Param("user") User user);
}
