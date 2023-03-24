package com.chirko.onLine.controller;

import com.chirko.onLine.dto.request.OldPasswordDto;
import com.chirko.onLine.dto.request.ResetUserPasswordDto;
import com.chirko.onLine.dto.request.UpdatePasswordDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.exception.InvalidOldPasswordException;
import com.chirko.onLine.exception.UserEmailNotFoundException;
import com.chirko.onLine.service.CommonTokenService;
import com.chirko.onLine.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final CommonTokenService commonTokenService;

    @GetMapping("/password/reset-form")
    public ResponseEntity<String> getResetPasswordForm(
            @RequestParam("token") String token
    ) throws Exception {

        commonTokenService.validateToken(token);

        return ResponseEntity.ok("Fill reset password form");
    }

    @PostMapping("/password/reset/save")
    public ResponseEntity<AuthenticationResponseDto> saveResetPassword(
            @RequestBody @Valid ResetUserPasswordDto resetUserPasswordDto
    ) throws Exception {

        AuthenticationResponseDto response = userService.saveResetPassword(resetUserPasswordDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestParam("email") String email
    ) throws UserEmailNotFoundException {

        userService.resetPassword(email);

        return ResponseEntity.ok("Follow the link sent to the email to reset your password");
    }

    @PostMapping("/password/update-form")
    public ResponseEntity<String> getUpdatePasswordForm(
            @RequestBody OldPasswordDto oldPasswordDto,
            Principal principal
    ) throws Exception {

        if (userService.isOldPasswordValid(principal.getName(), oldPasswordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }

        return ResponseEntity.ok("Fill update password form");
    }

    @PostMapping("/password/update")
    public ResponseEntity<String> updatePassword(
            @RequestBody @Valid UpdatePasswordDto updatePasswordDto,
            Principal principal
    ) throws UserEmailNotFoundException {

        userService.updatePassword(principal.getName(), updatePasswordDto.getPassword());

        return ResponseEntity.ok("Password successful updated");
    }

    @PostMapping("/avatar/update")
    public ResponseEntity<String> updateAvatar(
            @RequestParam("image") MultipartFile avatar,
            Principal principal
    ) throws UserEmailNotFoundException {

        userService.updateAvatar(avatar, principal.getName());

        return ResponseEntity.ok("Avatar updated");
    }
}
