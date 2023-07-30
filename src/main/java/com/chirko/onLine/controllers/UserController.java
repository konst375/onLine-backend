package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.RQViewedPostsDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PutMapping("/avatar/update")
    public ResponseEntity<UserPageDto> updateAvatar(@RequestParam("avatar") MultipartFile avatar,
                                                    @AuthenticationPrincipal User user) {
        UserPageDto response = userService.updateAvatar(user.getId(), avatar);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cover/update")
    public ResponseEntity<UserPageDto> updateCover(@RequestParam("cover") MultipartFile cover,
                                                   @AuthenticationPrincipal User user) {
        UserPageDto response = userService.updateCover(user.getId(), cover);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserPageDto> getUserPage(@PathVariable UUID userId) {
        UserPageDto response = userService.getUserPage(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return ResponseEntity.ok("Successful deleted");
    }

    @PostMapping("/mark-viewed-posts")
    public ResponseEntity<String> markPostsViewed(@ModelAttribute RQViewedPostsDto dto,
                                                  @AuthenticationPrincipal User user) {
        userService.markPostsViewed(user.getId(), dto.getViewedPostsIds());
        return ResponseEntity.ok("Successful marked");
    }

    @PostMapping("/log-activity")
     public ResponseEntity<String> logTheActiveDay(@AuthenticationPrincipal User user) {
        userService.logTheActiveDay(user);
        return ResponseEntity.ok("Successful logged");
    }
}
