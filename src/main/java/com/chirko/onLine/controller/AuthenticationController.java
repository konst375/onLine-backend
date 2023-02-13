package com.chirko.onLine.controller;

import com.chirko.onLine.dto.AuthenticationRequest;
import com.chirko.onLine.dto.RegisterRequest;
import com.chirko.onLine.exceptions.UserAlreadyExitsException;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.service.AuthenticationService;
import com.chirko.onLine.service.utils.AuthenticationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (UserAlreadyExitsException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (UserEmailNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
