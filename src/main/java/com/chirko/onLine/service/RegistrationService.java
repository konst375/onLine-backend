package com.chirko.onLine.service;

import com.chirko.onLine.dto.request.RegisterRequestDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.entity.Img;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.event.OnResendingConfirmationLinkEvent;
import com.chirko.onLine.event.OnSuccessfulRegistrationEvent;
import com.chirko.onLine.exception.ErrorCause;
import com.chirko.onLine.exception.OnLineException;
import com.chirko.onLine.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CommonTokenService commonTokenService;
    private final AccessTokenService accessTokenService;
    private final ImgService imgService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void register(RegisterRequestDto registerRequestDto) {
        if (userRepo.existsByEmail(registerRequestDto.getEmail())) {
            throw new OnLineException("User with this email already exist, email: " + registerRequestDto.getEmail(),
                    ErrorCause.USER_ALREADY_EXIST, HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .name(registerRequestDto.getName())
                .surname(registerRequestDto.getSurname())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .birthday(registerRequestDto.getBirthday())
                .build();
        user.setImagesList(List.of(
                Img.builder()
                        .isAvatar(true)
                        .img(imgService.getBytes(registerRequestDto.getAvatar()))
                        .user(user)
                        .build()));
        userRepo.save(user);

        String token = commonTokenService.createSaveAndGetCommonTokenForUser(user);
        applicationEventPublisher.publishEvent(new OnSuccessfulRegistrationEvent(user, token));
    }

    public void resendRegistrationToken(String expiredToken) {
        User user = extractUserFromToken(expiredToken);

        String token = commonTokenService.createNewCommonTokenForUser(user);

        applicationEventPublisher.publishEvent(new OnResendingConfirmationLinkEvent(user, token));
    }

    @Transactional
    public AuthenticationResponseDto confirmRegistration(String token) {
        commonTokenService.validateToken(token);

        User user = extractUserFromToken(token);

        user.setEnabled(true);

        commonTokenService.deleteCommonTokenForUser(user);

        return AuthenticationResponseDto.builder()
                .jwtToken(accessTokenService.generateAccessToken(user))
                .build();
    }

    private User extractUserFromToken(String token) {
        return commonTokenService.extractUser(token);
    }
}
