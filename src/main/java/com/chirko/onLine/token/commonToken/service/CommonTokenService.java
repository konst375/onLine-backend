package com.chirko.onLine.token.commonToken.service;

import com.chirko.onLine.token.commonToken.entity.CommonToken;
import com.chirko.onLine.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.token.commonToken.exception.InvalidCommonTokenException;
import com.chirko.onLine.token.commonToken.repo.CommonTokenRepo;
import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.user.exception.UserEmailNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

    public void validateToken(String validatedToken) throws CommonTokenExpiredException, InvalidCommonTokenException {
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
        if (token.getExpireDate().isBefore(LocalDate.now())) {
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
                        .expireDate(LocalDate.now().plusDays(1))
                        .build()
        );
    }
}
