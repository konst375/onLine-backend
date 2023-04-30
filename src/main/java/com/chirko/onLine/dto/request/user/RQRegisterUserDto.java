package com.chirko.onLine.dto.request.user;

import com.chirko.onLine.dto.validation.annotation.PasswordMatches;
import com.chirko.onLine.dto.validation.annotation.ValidEmail;
import com.chirko.onLine.dto.validation.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Setter
@Getter
@PasswordMatches
public class RQRegisterUserDto {
    private String name;
    private String surname;
    @NotNull
    @NotBlank
    @ValidEmail
    private String email;
    @NotNull
    @NotBlank
    @ValidPassword
    private String password;
    private String matchingPassword;
    private LocalDate birthday;
    private MultipartFile avatar;
}
