package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Like;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LikeRepo extends CrudRepository<Like, UUID> {
    @Modifying
    @Query("""
            DELETE
            FROM Like l
            WHERE l.img.id = :id OR l.post.id = :id OR l.comment.id = :id
            """)
    void deleteByParentId(@Param("id") UUID id);
}
