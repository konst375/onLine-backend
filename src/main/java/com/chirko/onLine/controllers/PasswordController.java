package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.OldPasswordRequestDto;
import com.chirko.onLine.dto.request.user.ResetUserPasswordRequestDto;
import com.chirko.onLine.dto.request.user.UpdateUserPasswordRequestDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.services.PasswordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user/password")
public class PasswordController {
    private final PasswordService passwordService;

    @GetMapping("/reset/form")
    public ResponseEntity<String> getResetPasswordForm(@RequestParam("token") String token) {
        passwordService.validateToken(token);
        return ResponseEntity.ok("Fill reset password form");
    }

    @PostMapping("/reset/save")
    public ResponseEntity<String> saveResetPassword(@RequestBody @Valid ResetUserPasswordRequestDto dto) {
        passwordService.saveResetPassword(dto);
        return ResponseEntity.ok("Password successful reset");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email) {
        passwordService.resetPassword(email);
        return ResponseEntity.ok("Follow the link sent to the email to reset your password");
    }

    @PostMapping("/update/form")
    public ResponseEntity<String> getUpdatePasswordForm(@RequestBody OldPasswordRequestDto dto,
                                                        @AuthenticationPrincipal User user) {
        if (!passwordService.isOldPasswordValid(user, dto.getOldPassword())) {
            throw new OnLineException("The old password you entered is invalid",
                    ErrorCause.OLD_PASSWORD_INVALID, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Fill update password form");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid UpdateUserPasswordRequestDto dto,
                                                 @AuthenticationPrincipal User user) {
        passwordService.updatePassword(user.getId(), dto.getPassword());
        return ResponseEntity.ok("Password successful updated");
    }
}
