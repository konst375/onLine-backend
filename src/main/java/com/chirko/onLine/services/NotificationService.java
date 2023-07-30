package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.NotificationMapper;
import com.chirko.onLine.dto.response.NotificationDto;
import com.chirko.onLine.entities.Notification;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.NotificationType;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.NotificationRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepo notificationRepo;
    private final NotificationMapper notificationMapper;

    public void saveFriendshipNotification(User user, NotificationType type, UUID friendId) {
        notificationRepo.save(Notification.builder()
                .user(user)
                .type(type)
                .target(friendId)
                .build()
        );
    }

    public Set<NotificationDto> getNotifications(User user) {
        Set<Notification> notifications = notificationRepo.findAllByUser(user).orElseThrow(() -> new OnLineException(
                "Notifications not found",
                ErrorCause.NOTIFICATION_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        return notificationMapper.toDtoSet(notifications);
    }
}
