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
    public ResponseEntity<String> updateAvatar(@RequestParam("image") MultipartFile avatar,
                                               @AuthenticationPrincipal User user) {
        userService.updateAvatar(user, avatar);
        return ResponseEntity.ok("Avatar successful updated");
    }

    @PutMapping("/cover/update")
    public ResponseEntity<String> updateCover(@RequestParam("cover") MultipartFile cover,
                                               @AuthenticationPrincipal User user) {
        userService.updateCover(user, cover);
        return ResponseEntity.ok("Cover successful updated");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPageDto> getUserPage(@PathVariable(name = "id") UUID userId) {
        UserPageDto userPageDto = userService.getUserPage(userId);
        return ResponseEntity.ok(userPageDto);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return ResponseEntity.ok("Successful deleted");
    }
}
