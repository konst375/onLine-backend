package com.chirko.onLine.services;

import com.chirko.onLine.dto.request.RQAuthenticationDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.UserRepo;
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

    public AuthenticationResponseDto authenticate(RQAuthenticationDto request) {
        String email = request.getEmail();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new OnLineException("User not found, email: " + email, ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        return new AuthenticationResponseDto(accessTokenService.generateAccessToken(user));
    }
}
