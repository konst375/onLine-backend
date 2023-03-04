package com.chirko.onLine.user.service;

import com.chirko.onLine.user.dto.ResetUserPasswordDto;
import com.chirko.onLine.user.event.OnPasswordResetRequestEvent;
import com.chirko.onLine.user.event.OnSuccessfulPasswordResetEvent;
import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.token.commonToken.exception.InvalidCommonToken;
import com.chirko.onLine.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.common.exception.UserEmailNotFoundException;
import com.chirko.onLine.user.repo.UserRepo;
import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.token.accessToken.service.AccessTokenService;
import com.chirko.onLine.token.commonToken.service.CommonTokenService;
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
    private final AccessTokenService accessTokenService;
    private final CommonTokenService commonTokenService;

    public void resetPassword(String email) throws UserEmailNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(UserEmailNotFoundException::new);

        String token = commonTokenService.createSaveAndGetCommonTokenForUser(user);

        applicationEventPublisher.publishEvent(new OnPasswordResetRequestEvent(user, token));
    }

    @Transactional
    public AuthenticationResponse saveResetPassword(
            @Valid ResetUserPasswordDto resetUserPasswordDto
    ) throws UserEmailNotFoundException, CommonTokenExpiredException, InvalidCommonToken, CommonTokenForSuchUserNotFoundException {

        String token = resetUserPasswordDto.getToken();
        commonTokenService.validateToken(token);

        User user = commonTokenService.extractUser(token);

        user.setPassword(passwordEncoder.encode(resetUserPasswordDto.getPassword()));

        applicationEventPublisher.publishEvent(new OnSuccessfulPasswordResetEvent(user));

        commonTokenService.deleteCommonTokenForUser(user);

        return AuthenticationResponse.builder()
                .jwtToken(accessTokenService.generateAccessToken(user))
                .build();
    }
}
