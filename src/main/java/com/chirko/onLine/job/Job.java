package com.chirko.onLine.job;

import com.chirko.onLine.entities.enums.NotificationType;
import com.chirko.onLine.services.FriendshipService;
import com.chirko.onLine.services.NotificationService;
import com.chirko.onLine.services.UserService;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@AllArgsConstructor
public class Job {
    private final UserService userService;
    private final NotificationService notificationService;
    private final FriendshipService friendshipService;

    @Scheduled(cron = "@hourly")
    @SchedulerLock(name = "friendsBirthdayNotice")
    public void createFriendsBirthdayNotifications() {
        LocalDateTime localDateTime = LocalDateTime.now();
        userService.getTimezones().forEach(zone -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(zone));
            if (zonedDateTime.getHour() == 3) {
                userService.findAllByTimezone(zone).forEach(user -> friendshipService.getFriends(user)
                        .forEach(friend -> {
                            LocalDate birthday = friend.getBirthday();
                            if (birthday.getMonthValue() == localDateTime.getMonthValue()
                                    && birthday.getDayOfMonth() == localDateTime.getDayOfMonth()) {
                                notificationService.saveFriendshipNotification(
                                        user,
                                        NotificationType.FRIEND_BIRTHDAY,
                                        friend.getId());
                            }
                        })
                );
            }
        });
    }
}
