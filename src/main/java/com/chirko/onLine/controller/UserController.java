package com.chirko.onLine.controller;

import com.chirko.onLine.exceptions.TokenExpiredException;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.service.common.AuthenticationResponse;
import com.chirko.onLine.service.user.UserService;
import com.chirko.onLine.service.user.dto.ResetUserPasswordDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/resetPasswordForm")
    public ResponseEntity<String> getChangePasswordForm() {
        return ResponseEntity.ok("Fill reset password form");
    }

    @PostMapping("/changePassword")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @RequestBody @Valid ResetUserPasswordDto resetUserPasswordDto
    ) throws UserEmailNotFoundException, TokenExpiredException {

        return ResponseEntity.ok(userService.changePassword(resetUserPasswordDto));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
            @RequestParam("email") String email
    ) throws UserEmailNotFoundException {

        userService.resetPassword(email);

        return ResponseEntity.ok("Follow the link sent to the email to reset your password");
    }
}
