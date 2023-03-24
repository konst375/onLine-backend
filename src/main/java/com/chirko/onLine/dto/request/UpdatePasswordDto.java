package com.chirko.onLine.dto.request;

import com.chirko.onLine.validation.annotation.PasswordMatches;
import com.chirko.onLine.validation.annotation.ValidPassword;
import lombok.Getter;

@Getter
@PasswordMatches
public class UpdatePasswordDto {
    @ValidPassword
    String password;
    String matchingPassword;
}
