package com.chirko.onLine.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtAuthRequestDto {
    private String refreshToken;
}
