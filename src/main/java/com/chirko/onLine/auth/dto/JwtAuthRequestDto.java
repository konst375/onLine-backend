package com.chirko.onLine.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtAuthRequestDto {
    private String email;
    private String password;
}
