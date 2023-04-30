package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.Post;
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
    @EntityGraph(attributePaths = {"tags", "images"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Post> findByIdAndFetchTagsAndImagesEagerly(@Param("id") UUID id);

    @Query("""
            SELECT u.images
            FROM User u
            WHERE u.id = (SELECT p.user.id FROM Post p WHERE p.id = :id)
            """)
    Optional<Set<Img>> findUserImagesByPost(@Param("id") UUID id);
}
