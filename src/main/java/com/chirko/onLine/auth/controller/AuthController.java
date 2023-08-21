package com.chirko.onLine.auth.controller;

import com.chirko.onLine.auth.dto.JwtAuthRequestDto;
import com.chirko.onLine.auth.dto.JwtAuthResponseDto;
import com.chirko.onLine.auth.dto.RefreshJwtAuthRequestDto;
import com.chirko.onLine.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<JwtAuthResponseDto> login(@RequestBody JwtAuthRequestDto authRequest) {
        final JwtAuthResponseDto token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("token")
    public ResponseEntity<JwtAuthResponseDto> getNewAccessToken(@RequestBody RefreshJwtAuthRequestDto request) {
        final JwtAuthResponseDto token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtAuthResponseDto> getNewRefreshToken(@RequestBody RefreshJwtAuthRequestDto request) {
        final JwtAuthResponseDto token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
