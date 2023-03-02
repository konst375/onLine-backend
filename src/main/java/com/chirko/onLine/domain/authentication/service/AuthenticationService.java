package com.chirko.onLine.domain.authentication.service;

import com.chirko.onLine.domain.authentication.dto.AuthenticationRequestDto;
import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.token.accessToken.service.AccessTokenService;
import com.chirko.onLine.domain.user.entity.User;
import com.chirko.onLine.common.exception.UserEmailNotFoundException;
import com.chirko.onLine.domain.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final AccessTokenService accessTokenService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse authenticate(AuthenticationRequestDto request) throws UserEmailNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(UserEmailNotFoundException::new);

        return AuthenticationResponse.builder()
                .jwtToken(accessTokenService.generateAccessToken(user))
                .build();
    }
}
