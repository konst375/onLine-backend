package com.chirko.onLine.dto.request;

import com.chirko.onLine.validation.annotation.PasswordMatches;
import com.chirko.onLine.validation.annotation.ValidEmail;
import com.chirko.onLine.validation.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class RegisterRequestDto {
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
