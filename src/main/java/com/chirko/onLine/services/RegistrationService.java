package com.chirko.onLine.services;

import com.chirko.onLine.dto.request.user.RQRegisterUserDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.events.OnResendingConfirmationLinkEvent;
import com.chirko.onLine.events.OnSuccessfulRegistrationEvent;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final CommonTokenService commonTokenService;
    private final AccessTokenService accessTokenService;
    private final ImgService imgService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    public void register(RQRegisterUserDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new OnLineException("User with this email already exist, email: " + dto.getEmail(),
                    ErrorCause.USER_ALREADY_EXIST, HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .birthday(dto.getBirthday())
                .build();
        if (dto.getAvatar() != null) {
                user.setImages(Set.of(imgService.buildUserAvatar(dto.getAvatar(), user)));
        }
        userRepo.save(user);
        String token = commonTokenService.getCommonToken(user);
        applicationEventPublisher.publishEvent(new OnSuccessfulRegistrationEvent(user, token));
    }

    public void resendRegistrationToken(String expiredToken) {
        User user = extractUserFromToken(expiredToken);
        String token = commonTokenService.generateNewCommonToken(user);
        applicationEventPublisher.publishEvent(new OnResendingConfirmationLinkEvent(user, token));
    }

    @Transactional
    public AuthenticationResponseDto confirmRegistration(String token) {
        commonTokenService.validateToken(token);
        User user = extractUserFromToken(token);
        user.setEnabled(true);
        commonTokenService.deleteCommonTokenForUser(user);
        return new AuthenticationResponseDto(accessTokenService.generateAccessToken(user));
    }

    private User extractUserFromToken(String token) {
        return commonTokenService.extractUser(token);
    }
}
