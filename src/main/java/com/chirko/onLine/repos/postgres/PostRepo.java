package com.chirko.onLine.repos.postgres;

import com.chirko.onLine.entities.Community;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
            WHERE p.id = :id
            """)
    @EntityGraph(attributePaths = "tags")
    Optional<Post> findByIdWithTags(UUID id);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.user = :user
            """)
    @EntityGraph(attributePaths = {"tags", "images", "likes"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Set<Post>> findAllByAdminWithTagsImagesAndLikes(@Param("user") User user);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.community IN :userCommunities
            AND p.createdDate >= :startDate
            """)
    @EntityGraph(attributePaths = {"tags", "images", "likes", "comments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Set<Post>> findCommunitiesPostsAfterStartDate(@Param("userCommunities") Set<Community> userCommunities,
                                                           @Param("startDate") Date startDate);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.user IN :userFriends
            AND p.createdDate >= :startDate
            """)
    @EntityGraph(attributePaths = {"tags", "images", "likes", "comments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Set<Post>> findUsersPostsAfterStartDate(@Param("userFriends") Set<User> userFriends,
                                                     @Param("startDate") Date startDate);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN p.tags tags
            WHERE tags IN :tags
            AND p.createdDate >= :startDate
            """)
    @EntityGraph(attributePaths = {"tags", "images", "likes", "comments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Set<Post>> findRecommendations(@Param("tags") Set<Tag> tags,
                                            @Param("startDate") Date startDate);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.id IN :postsIds
            """)
    @EntityGraph(attributePaths = {"tags", "images", "likes", "comments", "viewers"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Set<Post>> findAllByIdsWithAllDependencies(@Param("postsIds") Set<UUID> postsIds);
}
