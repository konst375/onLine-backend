package com.chirko.onLine.registration.controller;

import com.chirko.onLine.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.token.commonToken.exception.InvalidCommonToken;
import com.chirko.onLine.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.registration.exception.UserAlreadyExitsException;
import com.chirko.onLine.common.exception.UserEmailNotFoundException;
import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.registration.service.RegistrationService;
import com.chirko.onLine.registration.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private static final String DEFAULT_CONFIRMATION_MESSAGE = "Confirm your email by following the link we sent you.";

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegisterRequestDto registerRequestDto
    ) throws UserAlreadyExitsException {

        registrationService.register(registerRequestDto);

        return new ResponseEntity<>(DEFAULT_CONFIRMATION_MESSAGE, HttpStatus.CREATED);
    }

    @GetMapping("/confirm")
    public ResponseEntity<AuthenticationResponse> confirmRegistration(
            @RequestParam("token") String token
    ) throws UserEmailNotFoundException, CommonTokenExpiredException, InvalidCommonToken, CommonTokenForSuchUserNotFoundException {

        AuthenticationResponse response = registrationService.confirmRegistration(token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/resend-token")
    public ResponseEntity<String> resendRegistrationToken(
            @RequestParam("token") String expiredToken
    ) throws UserEmailNotFoundException, CommonTokenForSuchUserNotFoundException {

        registrationService.resendRegistrationToken(expiredToken);

        return ResponseEntity.ok(DEFAULT_CONFIRMATION_MESSAGE);
    }
}
