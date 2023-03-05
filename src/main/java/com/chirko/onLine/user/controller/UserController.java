package com.chirko.onLine.user.controller;

import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.token.commonToken.exception.InvalidCommonTokenException;
import com.chirko.onLine.token.commonToken.service.CommonTokenService;
import com.chirko.onLine.user.dto.OldPasswordDto;
import com.chirko.onLine.user.dto.ResetUserPasswordDto;
import com.chirko.onLine.user.dto.UpdatePasswordDto;
import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.user.exception.InvalidOldPasswordException;
import com.chirko.onLine.user.exception.UserEmailNotFoundException;
import com.chirko.onLine.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final CommonTokenService commonTokenService;

    @GetMapping("/password/reset-form")
    public ResponseEntity<String> getResetPasswordForm(
            @RequestParam("token") String token
    ) throws CommonTokenExpiredException, InvalidCommonTokenException {

        commonTokenService.validateToken(token);

        return ResponseEntity.ok("Fill reset password form");
    }

    @PostMapping("/password/reset/save")
    public ResponseEntity<AuthenticationResponse> saveResetPassword(
            @RequestBody @Valid ResetUserPasswordDto resetUserPasswordDto
    ) throws CommonTokenExpiredException,
             InvalidCommonTokenException,
             UserEmailNotFoundException,
             CommonTokenForSuchUserNotFoundException {

        AuthenticationResponse response = userService.saveResetPassword(resetUserPasswordDto);

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
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public  ResponseEntity<String> getUpdatePasswordForm(
            @RequestBody OldPasswordDto oldPasswordDto
    ) throws UserEmailNotFoundException, InvalidOldPasswordException {

        User user = userService.findUserByEmail(getAuthenticatedUserEmail());

        if (userService.isOldPasswordValid(user, oldPasswordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }

        return ResponseEntity.ok("Fill update password form");
    }

    @PostMapping("/password/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public ResponseEntity<String> updatePassword(
            @RequestBody @Valid UpdatePasswordDto updatePasswordDto
    ) throws UserEmailNotFoundException {

        userService.updatePassword(getAuthenticatedUserEmail(), updatePasswordDto.getPassword());

        return ResponseEntity.ok("Password successful updated");
    }

    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
