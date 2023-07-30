package com.chirko.onLine.repos;

import com.chirko.onLine.entities.UserActivity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserActivityRepo extends CrudRepository<UserActivity, UUID> {
    boolean existsByUserIdAndActivityDate(UUID userId, Date activityDate);

    @Query("""
            SELECT ua.createdDate
            FROM UserActivity ua
            WHERE ua.user.id = :userId
            ORDER BY ua.activityDate
            LIMIT 3
            """)
    Optional<List<Date>> findStartSessionDateByUserId(@Param("userId") UUID userId);
}
