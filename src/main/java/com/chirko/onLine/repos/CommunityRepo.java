package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Community;
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
public interface CommunityRepo extends CrudRepository<Community, UUID> {
    Optional<Set<Community>> findByAdmin(User user);

    @Query("""
            SELECT c.followers
            FROM Community c
            WHERE c.id = :id
            """)
    Optional<Set<User>> findFollowersByCommunityId(@Param("id") UUID id);

    @Query("""
            SELECT c
            FROM Community c
            WHERE c.id = :id
            """)
    @EntityGraph(attributePaths = {"images"})
    Optional<Community> findByIdAndFetchImagesEagerly(@Param("id") UUID id);

    @Query("""
            SELECT c
            FROM Community c
            WHERE c.id = :id
            """)
    @EntityGraph(value = "Community.dependencies")
    Optional<Community> findByIdAndFetchAllDependencies(@Param("id") UUID id);
}
