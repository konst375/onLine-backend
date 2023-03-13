package com.chirko.onLine.secure.authentication.controller;

import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.secure.authentication.dto.AuthenticationRequest;
import com.chirko.onLine.secure.authentication.service.AuthenticationService;
import com.chirko.onLine.user.exception.UserEmailNotFoundException;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) throws UserEmailNotFoundException {
            return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
