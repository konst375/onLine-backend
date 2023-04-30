package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.user.RQRegisterUserDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/registration")
public class RegistrationController {
    private final RegistrationService registrationService;
    private static final String DEFAULT_CONFIRMATION_MESSAGE = "Confirm your email by following the link we sent you.";

    @PostMapping("/register")
    public ResponseEntity<String> register(@ModelAttribute @Valid RQRegisterUserDto dto) {
        registrationService.register(dto);
        return new ResponseEntity<>(DEFAULT_CONFIRMATION_MESSAGE, HttpStatus.CREATED);
    }

    @GetMapping("/confirm")
    public ResponseEntity<AuthenticationResponseDto> confirmRegistration(@RequestParam("token") String token) {
        AuthenticationResponseDto response = registrationService.confirmRegistration(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resend-token")
    public ResponseEntity<String> resendRegistrationToken(@RequestParam("token") String expiredToken) {
        registrationService.resendRegistrationToken(expiredToken);
        return ResponseEntity.ok(DEFAULT_CONFIRMATION_MESSAGE);
    }
}
