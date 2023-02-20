package com.chirko.onLine.service;

import com.chirko.onLine.common.authentication.AuthenticationResponse;
import com.chirko.onLine.common.registration.event.OnRegistrationCompleteEvent;
import com.chirko.onLine.dto.RegisterRequestDto;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.entity.enums.Role;
import com.chirko.onLine.exceptions.UserAlreadyExitsException;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.repo.UserRepo;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void register(
            RegisterRequestDto registerRequest
    ) throws UserAlreadyExitsException {

        if (userRepo.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExitsException("User with this email already exist");
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .surname(registerRequest.getSurname())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .birthday(registerRequest.getBirthday())
                .role(Role.USER)
                .avatar(registerRequest.getAvatar())
                .build();

        userRepo.save(user);

        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
    }

    @Transactional
    public AuthenticationResponse confirmRegistration(
            String token
    ) throws UserEmailNotFoundException, ExpiredJwtException {
        User user = userRepo.findByEmail(tokenService.extractEmail(token))
                .orElseThrow(() -> new UserEmailNotFoundException("User with this email does not exist"));

        if (!tokenService.isTokenValid(token, user)) {
            throw new ExpiredJwtException(
                    null,
                    tokenService.extractAllClaims(token),
                    String.format(
                            "Error validating access token: Session has expired on %s. The current time is %s.",
                            tokenService.extractExpiration(token), new Date()));
        }

        user.setEnabled(true);

        return AuthenticationResponse.builder()
                .jwtToken(token)
                .build();
    }
}
