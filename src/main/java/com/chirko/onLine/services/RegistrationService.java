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

import java.util.List;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final OtpService otpService;
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
                .images(dto.getAvatar() == null ? null : List.of(imgService.createAvatar(dto.getAvatar())))
                .timezone(dto.getTimezone())
                .build();
        if (user.getImages() != null) {
            user.getImages().forEach(img -> img.setUser(user));
        }
        User savedUser = userRepo.save(user);
        String token = otpService.getCommonToken(user);
        applicationEventPublisher.publishEvent(new OnSuccessfulRegistrationEvent(savedUser, token));
    }

    public void resendRegistrationToken(String expiredToken) {
        User user = extractUserFromToken(expiredToken);
        String token = otpService.generateNewCommonToken(user);
        applicationEventPublisher.publishEvent(new OnResendingConfirmationLinkEvent(user, token));
    }

    @Transactional
    public AuthenticationResponseDto confirmRegistration(String token) {
        otpService.validateToken(token);
        User user = extractUserFromToken(token);
        user.setEnabled(true);
        otpService.deleteCommonTokenForUser(user);
        return new AuthenticationResponseDto(accessTokenService.generateAccessToken(user));
    }

    private User extractUserFromToken(String token) {
        return otpService.extractUser(token);
    }
}
