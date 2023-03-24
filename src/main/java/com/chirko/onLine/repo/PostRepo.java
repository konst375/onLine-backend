package com.chirko.onLine.repo;

import com.chirko.onLine.entity.Post;
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
            JOIN FETCH p.imagesList
            WHERE p.id = (:id)
            """)
    Optional<Post> findByIdAndFetchImagesEagerly(@Param("id") UUID id);
}
