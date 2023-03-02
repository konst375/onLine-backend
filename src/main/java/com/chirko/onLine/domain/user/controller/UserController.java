package com.chirko.onLine.domain.user.controller;

import com.chirko.onLine.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.token.commonToken.exception.InvalidCommonToken;
import com.chirko.onLine.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.common.exception.UserEmailNotFoundException;
import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.token.commonToken.service.CommonTokenService;
import com.chirko.onLine.domain.user.service.UserService;
import com.chirko.onLine.domain.user.dto.ResetUserPasswordDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final CommonTokenService commonTokenService;

    @GetMapping("/password/reset-form")
    public ResponseEntity<String> getChangePasswordForm(
            @RequestParam("token") String token
    ) throws CommonTokenExpiredException, InvalidCommonToken {
        commonTokenService.validateToken(token);
        return ResponseEntity.ok("Fill reset password form");
    }

    @PostMapping("/password/reset/save")
    public ResponseEntity<AuthenticationResponse> saveResetPassword(
            @RequestBody @Valid ResetUserPasswordDto resetUserPasswordDto
    ) throws CommonTokenExpiredException, InvalidCommonToken, UserEmailNotFoundException, CommonTokenForSuchUserNotFoundException {

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
}
