package com.chirko.onLine.services;

import com.chirko.onLine.entities.Otp;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.OtpRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OtpService {
    public static final long FIVE_MIN = 300000;
    private final OtpRepo otpRepo;

    public void validateToken(String validatedToken) {
        Otp token = otpRepo.findByToken(validatedToken)
                .orElseThrow(() -> new OnLineException(
                        "Invalid common token",
                        ErrorCause.COMMON_TOKEN_INVALID,
                        HttpStatus.BAD_REQUEST));
        checkTokenExpiration(token);
    }

    String getCommonToken(User user) {
        final String token = generateCommonToken();
        buildCommonToken(user, token);
        return token;
    }

    String generateNewCommonToken(User user) {
        deleteCommonTokenForUser(user);
        final String token = generateCommonToken();
        buildCommonToken(user, token);
        return token;
    }

    User extractUser(String token) {
        return otpRepo.findByToken(token)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMON_TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND))
                .getUser();
    }

    void deleteCommonTokenForUser(User user) {
        Otp otp = otpRepo.findByUser(user)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMON_TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND));
        otpRepo.delete(otp);
    }

    private void checkTokenExpiration(Otp token) {
        if (token.getExpireTimestamp().before(new Timestamp(System.currentTimeMillis()))) {
            throw new OnLineException("Confirmation time expired", ErrorCause.COMMON_TOKEN_EXPIRED, HttpStatus.GONE);
        }
    }

    private String generateCommonToken() {
        return UUID.randomUUID().toString();
    }

    private void buildCommonToken(User user, String token) {
        otpRepo.save(Otp.builder()
                .user(user)
                .token(token)
                .expireTimestamp(new Timestamp(System.currentTimeMillis() + FIVE_MIN))
                .build()
        );
    }
}
