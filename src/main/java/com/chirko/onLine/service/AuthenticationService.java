package com.chirko.onLine.service;

import com.chirko.onLine.dto.request.AuthenticationRequestDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exception.UserEmailNotFoundException;
import com.chirko.onLine.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final AccessTokenService accessTokenService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) throws UserEmailNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(UserEmailNotFoundException::new);

        return AuthenticationResponseDto.builder()
                .jwtToken(accessTokenService.generateAccessToken(user))
                .build();
    }
}
