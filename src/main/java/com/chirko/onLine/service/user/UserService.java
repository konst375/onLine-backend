package com.chirko.onLine.service.user;

import com.chirko.onLine.entity.User;
import com.chirko.onLine.exceptions.TokenExpiredException;
import com.chirko.onLine.exceptions.UserEmailNotFoundException;
import com.chirko.onLine.repo.UserRepo;
import com.chirko.onLine.service.common.AuthenticationResponse;
import com.chirko.onLine.service.token.TokenService;
import com.chirko.onLine.service.user.dto.ResetUserPasswordDto;
import com.chirko.onLine.service.user.event.OnPasswordResetRequestEvent;
import com.chirko.onLine.service.user.event.OnSuccessfulPasswordResetEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TokenService tokenService;

    public void resetPassword(String email) throws UserEmailNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(UserEmailNotFoundException::new);
        applicationEventPublisher.publishEvent(new OnPasswordResetRequestEvent(user));
    }

    @Transactional
    public AuthenticationResponse changePassword(@Valid ResetUserPasswordDto resetUserPasswordDto) throws UserEmailNotFoundException, TokenExpiredException {
        String token = resetUserPasswordDto.getToken();
        String email = tokenService.extractEmail(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(UserEmailNotFoundException::new);

        tokenService.checkTokenExpirationDate(token);

        user.setPassword(passwordEncoder.encode(resetUserPasswordDto.getPassword()));

        applicationEventPublisher.publishEvent(new OnSuccessfulPasswordResetEvent(user));

        return AuthenticationResponse.builder()
                .jwtToken(tokenService.generateAccessToken(user))
                .build();
    }
}
