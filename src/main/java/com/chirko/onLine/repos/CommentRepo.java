package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Comment;
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
public interface CommentRepo extends CrudRepository<Comment, UUID> {
    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.post = :post
            """)
    @EntityGraph(attributePaths = {"user.images"})
    Optional<Set<Comment>> findAllByPostAndFetchUserImagesEagerly(@Param("post") Post post);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.img = :img
            """)
    @EntityGraph(attributePaths = {"user.images"})
    Optional<Set<Comment>> findAllByImgAndFetchUserImagesEagerly(@Param("img") Img img);

    @Query("""
            SELECT c.user.images
            FROM Comment c
            WHERE c = :comment
            """)
    Optional<Set<Img>> findUserImages(@Param("comment") Comment comment);
}
