package com.chirko.onLine.repo;

import com.chirko.onLine.entity.Img;
import com.chirko.onLine.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImgRepo extends CrudRepository<Img, UUID> {
    Optional<Img> findByUser(User user);

    @Query("""
            SELECT i
            FROM Img i
            JOIN FETCH i.comments
            WHERE i.id = (:id)
            """)
    Optional<Img> findByIdAndFetchCommentsEagerly(@Param("id") UUID id);
}
