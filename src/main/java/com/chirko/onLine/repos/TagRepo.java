package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepo extends CrudRepository<Tag, UUID> {
    @Query("""
            SELECT
            CASE
                WHEN count(t) = 0
                    THEN TRUE
                ELSE FALSE
            END
            FROM Tag t WHERE t.tagName LIKE :tagName
            """)
    boolean notExistsByTagName(@Param("tagName") String tagName);

    @Query("""
            SELECT t
            FROM Tag t
            WHERE t.tagName LIKE :tagName
            """)
    @EntityGraph(attributePaths = {"posts"})
    Optional<Tag> findByTagNameAndFetchPostsEagerly(@Param("tagName") String tagName);

    @Query("""
            SELECT t
            FROM Tag t
            WHERE t.tagName LIKE :tagName
            """)
    @EntityGraph(attributePaths = {"communities"})
    Optional<Tag> findByTagNameAndFetchCommunitiesEagerly(@Param("tagName") String tagName);
}
