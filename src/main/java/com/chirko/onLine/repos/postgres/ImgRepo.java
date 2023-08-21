package com.chirko.onLine.repos.postgres;

import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImgRepo extends CrudRepository<Img, UUID> {
    Optional<List<Img>> findImagesByUser(User user);

    @Query("""
            SELECT i
            FROM Img i
            WHERE i.id = :id
            """)
    @EntityGraph(attributePaths = {"likes", "comments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Img> findByIdWithLikesAndComments(@Param("id") UUID id);
}
