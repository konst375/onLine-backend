package com.chirko.onLine.service;

import com.chirko.onLine.entity.CommonToken;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exception.ErrorCause;
import com.chirko.onLine.exception.OnLineException;
import com.chirko.onLine.repo.CommonTokenRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommonTokenService {
    private final CommonTokenRepo commonTokenRepo;

    public String createSaveAndGetCommonTokenForUser(User user) {

        final String token = generateCommonToken();
        buildAndSaveCommonToken(user, token);

        return token;
    }

    public String createNewCommonTokenForUser(User user) {

        deleteCommonTokenForUser(user);

        final String token = generateCommonToken();
        buildAndSaveCommonToken(user, token);

        return token;
    }

    public void validateToken(String validatedToken) {
        CommonToken token = commonTokenRepo.findByToken(validatedToken)
                .orElseThrow(() -> new OnLineException("Invalid common token", ErrorCause.COMMON_TOKEN_INVALID,
                        HttpStatus.BAD_REQUEST));
        checkTokenExpiration(token);
    }

    public User extractUser(String token) {
        return commonTokenRepo.findByToken(token)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMON_TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND))
                .getUser();
    }

    public void deleteCommonTokenForUser(User user) {
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

    private void buildAndSaveCommonToken(User user, String token) {
        commonTokenRepo.save(
                CommonToken.builder()
                        .user(user)
                        .token(token)
                        .expireTimestamp(new Timestamp(System.currentTimeMillis() + CommonToken.EXPIRATION))
                        .build()
        );
    }
}
