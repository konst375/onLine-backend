package com.chirko.onLine.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtAuthResponseDto {
    private final String type = "Bearer";
    private final String accessToken;
    private final String refreshToken;
}
