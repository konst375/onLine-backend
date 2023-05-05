package com.chirko.onLine.controllers;

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
    public ResponseEntity<UserPageDto> updateAvatar(@RequestParam("image") MultipartFile avatar,
                                                    @AuthenticationPrincipal UUID userId) {
        UserPageDto response = userService.updateAvatar(userId, avatar);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cover/update")
    public ResponseEntity<UserPageDto> updateCover(@RequestParam("cover") MultipartFile cover,
                                                   @AuthenticationPrincipal UUID userId) {
        UserPageDto response = userService.updateCover(userId, cover);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPageDto> getUserPage(@PathVariable(name = "id") UUID userId) {
        UserPageDto response = userService.getUserPage(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return ResponseEntity.ok("Successful deleted");
    }
}
