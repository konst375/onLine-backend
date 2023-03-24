package com.chirko.onLine.dto.request;

import com.chirko.onLine.validation.annotation.PasswordMatches;
import com.chirko.onLine.validation.annotation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class ResetUserPasswordDto {
    private String token;
    @ValidPassword
    private String password;
    private String matchingPassword;
}
