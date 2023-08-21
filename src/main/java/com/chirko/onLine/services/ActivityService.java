package com.chirko.onLine.services;

import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.UserActivity;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.postgres.UserActivityRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ActivityService {
    private static final long THEE_DAYS = 259200000;
    private final UserActivityRepo userActivityRepo;

    public Date getStartDateForUser(UUID userId) {
        long startSessionDate = userActivityRepo.findStartSessionDateByUserId(userId)
                .orElseThrow(() -> new OnLineException(
                        "UserActivity not found, userId: " + userId,
                        ErrorCause.USER_ACTIVITY_NOT_FOUND,
                        HttpStatus.INTERNAL_SERVER_ERROR))
                .getTime();
        return new Date(startSessionDate - THEE_DAYS);
    }

    public void logTheActiveDay(User user) {
        Date date = new Date(System.currentTimeMillis());
        if (!userActivityRepo.existsByUserIdAndActivityDate(user.getId(), date)) {
            userActivityRepo.save(UserActivity.builder().user(user).activityDate(date).build());
        }
    }
}
