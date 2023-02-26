package com.chirko.onLine.service.authentication;

import com.chirko.onLine.service.authentication.dto.AuthenticationRequestDto;
import com.chirko.onLine.service.common.AuthenticationResponse;
import com.chirko.onLine.service.token.TokenService;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final TokenService tokenService;
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
                .jwtToken(tokenService.generateAccessToken(user))
                .build();
    }
}
