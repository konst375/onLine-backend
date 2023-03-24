package com.chirko.onLine.service;

import com.chirko.onLine.entity.CommonToken;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exception.CommonTokenExpiredException;
import com.chirko.onLine.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.exception.InvalidCommonTokenException;
import com.chirko.onLine.exception.UserEmailNotFoundException;
import com.chirko.onLine.repo.CommonTokenRepo;
import lombok.AllArgsConstructor;
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

    public String createNewCommonTokenForUser(User user) throws CommonTokenForSuchUserNotFoundException {

        deleteCommonTokenForUser(user);

        final String token = generateCommonToken();
        buildAndSaveCommonToken(user, token);

        return token;
    }

    public void validateToken(String validatedToken) throws Exception {
        CommonToken token = commonTokenRepo.findByToken(validatedToken).orElseThrow(InvalidCommonTokenException::new);
        checkTokenExpiration(token);
    }

    public User extractUser(String token) throws UserEmailNotFoundException {
        return commonTokenRepo.findByToken(token).orElseThrow(UserEmailNotFoundException::new).getUser();
    }

    public void deleteCommonTokenForUser(User user) throws CommonTokenForSuchUserNotFoundException {
        CommonToken commonToken = commonTokenRepo.findByUser(user)
                .orElseThrow(CommonTokenForSuchUserNotFoundException::new);
        commonTokenRepo.delete(commonToken);
    }

    private void checkTokenExpiration(CommonToken token) throws CommonTokenExpiredException {
        if (token.getExpireTimestamp().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CommonTokenExpiredException();
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
