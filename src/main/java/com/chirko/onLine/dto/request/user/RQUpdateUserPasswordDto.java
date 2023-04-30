package com.chirko.onLine.dto.request.user;

import com.chirko.onLine.dto.validation.annotation.PasswordMatches;
import com.chirko.onLine.dto.validation.annotation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatches
public class RQUpdateUserPasswordDto {
    @ValidPassword
    private String password;
    private String matchingPassword;
}
