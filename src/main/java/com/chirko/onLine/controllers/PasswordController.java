package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.RQOldPasswordDto;
import com.chirko.onLine.dto.request.user.RQResetUserPasswordDto;
import com.chirko.onLine.dto.request.user.RQUpdateUserPasswordDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.services.CommonTokenService;
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
    private final CommonTokenService commonTokenService;

    @GetMapping("/reset-form")
    public ResponseEntity<String> getResetPasswordForm(@RequestParam("token") String token) {
        commonTokenService.validateToken(token);
        return ResponseEntity.ok("Fill reset password form");
    }

    @PostMapping("/reset/save")
    public ResponseEntity<AuthenticationResponseDto> saveResetPassword(@RequestBody @Valid RQResetUserPasswordDto dto) {
        AuthenticationResponseDto response = passwordService.saveResetPassword(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email) {
        passwordService.resetPassword(email);
        return ResponseEntity.ok("Follow the link sent to the email to reset your password");
    }

    @PostMapping("/update-form")
    public ResponseEntity<String> getUpdatePasswordForm(@RequestBody RQOldPasswordDto dto,
                                                        @AuthenticationPrincipal User user) {
        if (!passwordService.isOldPasswordValid(user, dto.getOldPassword())) {
            throw new OnLineException("The old password you entered is invalid",
                    ErrorCause.OLD_PASSWORD_INVALID, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Fill update password form");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid RQUpdateUserPasswordDto dto,
                                                 @AuthenticationPrincipal User user) {
        passwordService.updatePassword(user.getId(), dto.getPassword());
        return ResponseEntity.ok("Password successful updated");
    }
}
