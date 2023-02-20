package com.chirko.onLine.controller;

import com.chirko.onLine.common.authentication.AuthenticationResponse;
import com.chirko.onLine.dto.AuthenticationRequestDto;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.service.AuthenticationService;
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
            @RequestBody AuthenticationRequestDto request
    ) throws UserEmailNotFoundException {
            return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
