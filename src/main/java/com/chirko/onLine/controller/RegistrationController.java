package com.chirko.onLine.controller;

import com.chirko.onLine.common.authentication.AuthenticationResponse;
import com.chirko.onLine.dto.RegisterRequestDto;
import com.chirko.onLine.exceptions.UserAlreadyExitsException;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.service.RegistrationService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegisterRequestDto registerRequestDto
    ) throws UserAlreadyExitsException {

        registrationService.register(registerRequestDto);

        return ResponseEntity.ok("Confirm your email by following the link we sent you.");
    }

    @GetMapping("/registrationConfirm")
    public ResponseEntity<AuthenticationResponse> confirmRegistration(
            @RequestParam("token") String token
    ) throws UserEmailNotFoundException, ExpiredJwtException {

        return ResponseEntity.ok(registrationService.confirmRegistration(token));
    }
}
