package com.chirko.onLine.service.registration;

import com.chirko.onLine.entity.User;
import com.chirko.onLine.entity.enums.Role;
import com.chirko.onLine.exceptions.TokenExpiredException;
import com.chirko.onLine.exceptions.UserAlreadyExitsException;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.repo.UserRepo;
import com.chirko.onLine.service.common.AuthenticationResponse;
import com.chirko.onLine.service.registration.dto.RegisterRequestDto;
import com.chirko.onLine.service.registration.event.OnResendingConfirmationLinkEvent;
import com.chirko.onLine.service.registration.event.OnSuccessfulRegistrationEvent;
import com.chirko.onLine.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new UserAlreadyExitsException();
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

        applicationEventPublisher.publishEvent(new OnSuccessfulRegistrationEvent(user));
    }

    public void resendRegistrationToken(String expiredToken) throws UserEmailNotFoundException {
        User user = extractUserFromToken(expiredToken);
        applicationEventPublisher.publishEvent(new OnResendingConfirmationLinkEvent(user));
    }

    @Transactional
    public AuthenticationResponse confirmRegistration(
            String token
    ) throws UserEmailNotFoundException, TokenExpiredException {
        tokenService.checkTokenExpirationDate(token);

        User user = extractUserFromToken(token);
        user.setEnabled(true);

        return AuthenticationResponse.builder()
                .jwtToken(tokenService.generateAccessToken(user))
                .build();
    }

    private User extractUserFromToken(String token) throws UserEmailNotFoundException {
        return userRepo.findByEmail(tokenService.extractEmail(token))
                .orElseThrow(UserEmailNotFoundException::new);
    }
}
