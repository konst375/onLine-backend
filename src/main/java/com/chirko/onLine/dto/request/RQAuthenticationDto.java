package com.chirko.onLine.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RQAuthenticationDto {
    private String email;
    private String password;
}
