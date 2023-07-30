package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.response.FriendshipsDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.FriendshipService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/{recipientId}")
    public ResponseEntity<String> sendFriendRequest(@PathVariable UUID recipientId,
                                                    @AuthenticationPrincipal User sender) {
        friendshipService.sendFriendRequest(recipientId, sender);
        return new ResponseEntity<>("Request sent", HttpStatus.CREATED);
    }

    @PutMapping("/approve/{senderId}")
    public ResponseEntity<String> approveFriendship(@PathVariable UUID senderId,
                                                    @AuthenticationPrincipal User recipient) {
        friendshipService.approveFriendship(senderId, recipient);
        return ResponseEntity.ok("Approved");
    }

    @PutMapping("/deny/{senderId}")
    public ResponseEntity<String> denyFriendship(@PathVariable UUID senderId,
                                                       @AuthenticationPrincipal User recipient) {
        friendshipService.denyFriendship(senderId, recipient);
        return ResponseEntity.ok("Disapproved");
    }

    @GetMapping
    public ResponseEntity<FriendshipsDto> getFriendships(@AuthenticationPrincipal User user) {
        FriendshipsDto response = friendshipService.getFriendshipsDto(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable UUID friendId, @AuthenticationPrincipal User user) {
        friendshipService.deleteFriend(friendId, user);
        return ResponseEntity.ok("Deleted");
    }

    @DeleteMapping("/unsubscribe/{userId}")
    public ResponseEntity<String> unsubscribe(@PathVariable UUID userId, @AuthenticationPrincipal User user) {
        friendshipService.unsubscribe(userId, user);
        return ResponseEntity.ok("Deleted");
    }
}
