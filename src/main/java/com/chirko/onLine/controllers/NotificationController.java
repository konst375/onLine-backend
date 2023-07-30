package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.response.NotificationDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Set<NotificationDto>> getNotifications(@AuthenticationPrincipal User user) {
        Set<NotificationDto> response = notificationService.getNotifications(user);
        return ResponseEntity.ok(response);
    }
}
