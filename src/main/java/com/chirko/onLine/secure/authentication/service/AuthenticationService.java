package com.chirko.onLine.secure.authentication.service;

import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.secure.authentication.dto.AuthenticationRequest;
import com.chirko.onLine.secure.token.accessToken.service.AccessTokenService;
import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.user.exception.UserEmailNotFoundException;
import com.chirko.onLine.user.repo.UserRepo;
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


    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UserEmailNotFoundException {
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
