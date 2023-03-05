package com.chirko.onLine.user.dto;

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
