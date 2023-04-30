package com.chirko.onLine.services;

import com.chirko.onLine.entities.CommonToken;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.CommonTokenRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommonTokenService {
    private final CommonTokenRepo commonTokenRepo;

    public void validateToken(String validatedToken) {
        CommonToken token = commonTokenRepo.findByToken(validatedToken)
                .orElseThrow(() -> new OnLineException("Invalid common token", ErrorCause.COMMON_TOKEN_INVALID,
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
        return commonTokenRepo.findByToken(token)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMON_TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND))
                .getUser();
    }

    void deleteCommonTokenForUser(User user) {
        CommonToken commonToken = commonTokenRepo.findByUser(user)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMON_TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND));
        commonTokenRepo.delete(commonToken);
    }

    private void checkTokenExpiration(CommonToken token) {
        if (token.getExpireTimestamp().before(new Timestamp(System.currentTimeMillis()))) {
            throw new OnLineException("Confirmation time expired", ErrorCause.COMMON_TOKEN_EXPIRED, HttpStatus.GONE);
        }
    }

    private String generateCommonToken() {
        return UUID.randomUUID().toString();
    }

    private void buildCommonToken(User user, String token) {
        commonTokenRepo.save(CommonToken.builder()
                .user(user)
                .token(token)
                .expireTimestamp(new Timestamp(System.currentTimeMillis() + CommonToken.EXPIRATION))
                .build()
        );
    }
}
