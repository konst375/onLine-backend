package com.chirko.onLine.secure.registration.controller;

import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.secure.registration.dto.RegisterRequestDto;
import com.chirko.onLine.secure.registration.exception.UserAlreadyExitsException;
import com.chirko.onLine.secure.registration.service.RegistrationService;
import com.chirko.onLine.secure.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.secure.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.secure.token.commonToken.exception.InvalidCommonTokenException;
import com.chirko.onLine.user.exception.UserEmailNotFoundException;
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
            @ModelAttribute @Valid RegisterRequestDto registerRequestDto
            ) throws UserAlreadyExitsException {

        registrationService.register(registerRequestDto);

        return new ResponseEntity<>(DEFAULT_CONFIRMATION_MESSAGE, HttpStatus.CREATED);
    }

    @GetMapping("/confirm")
    public ResponseEntity<AuthenticationResponse> confirmRegistration(
            @RequestParam("token") String token
    ) throws UserEmailNotFoundException, CommonTokenExpiredException, InvalidCommonTokenException, CommonTokenForSuchUserNotFoundException {

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
