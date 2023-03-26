package com.chirko.onLine.service;

import com.chirko.onLine.dto.request.AuthenticationRequestDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exception.ErrorCause;
import com.chirko.onLine.exception.OnLineException;
import com.chirko.onLine.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final AccessTokenService accessTokenService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        String email = request.getEmail();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new OnLineException("User with this email does not exist, email: " + email,
                        ErrorCause.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        return AuthenticationResponseDto.builder()
                .jwtToken(accessTokenService.generateAccessToken(user))
                .build();
    }
}
