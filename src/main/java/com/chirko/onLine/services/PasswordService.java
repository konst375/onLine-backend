package com.chirko.onLine.services;

import com.chirko.onLine.dto.request.user.ResetUserPasswordRequestDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.events.OnPasswordResetRequestEvent;
import com.chirko.onLine.events.OnSuccessfulPasswordResetEvent;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.postgres.UserRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordService {
    private final OtpService otpService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    public boolean isOldPasswordValid(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Transactional
    public void updatePassword(UUID userId, String password) {
        userRepo.findById(userId).orElseThrow(() -> new OnLineException(
                        "User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND))
                .setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void saveResetPassword(@Valid ResetUserPasswordRequestDto dto) {
        String token = dto.getToken();
        validateToken(token);
        User user = otpService.extractUser(token);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        applicationEventPublisher.publishEvent(new OnSuccessfulPasswordResetEvent(user));
        otpService.deleteOtpForUser(user);
    }

    public void resetPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new OnLineException(
                        "User with this email does not exist, email: " + email,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        String token = otpService.getCommonToken(user);
        applicationEventPublisher.publishEvent(new OnPasswordResetRequestEvent(user, token));
    }

    public void validateToken(String token) {
        otpService.validateToken(token);
    }
}
