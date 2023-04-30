package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.RQAuthenticationDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody RQAuthenticationDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
