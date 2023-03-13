package com.chirko.onLine.secure.registration.service;

import com.chirko.onLine.common.dto.AuthenticationResponse;
import com.chirko.onLine.img.service.ImgService;
import com.chirko.onLine.secure.registration.dto.RegisterRequestDto;
import com.chirko.onLine.secure.registration.event.OnResendingConfirmationLinkEvent;
import com.chirko.onLine.secure.registration.event.OnSuccessfulRegistrationEvent;
import com.chirko.onLine.secure.registration.exception.UserAlreadyExitsException;
import com.chirko.onLine.secure.token.accessToken.service.AccessTokenService;
import com.chirko.onLine.secure.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.secure.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.secure.token.commonToken.exception.InvalidCommonTokenException;
import com.chirko.onLine.secure.token.commonToken.service.CommonTokenService;
import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.user.entity.enums.Role;
import com.chirko.onLine.user.exception.UserEmailNotFoundException;
import com.chirko.onLine.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CommonTokenService commonTokenService;
    private final AccessTokenService accessTokenService;
    private final ImgService imgService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void register(RegisterRequestDto registerRequest) throws UserAlreadyExitsException {

        if (userRepo.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExitsException();
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .surname(registerRequest.getSurname())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .birthday(registerRequest.getBirthday())
                .role(Role.USER)
                .build();

        userRepo.save(user);
        user.setAvatar(imgService.createAvatarForUser(registerRequest.getAvatar(), user));

        String token = commonTokenService.createSaveAndGetCommonTokenForUser(user);

        applicationEventPublisher.publishEvent(new OnSuccessfulRegistrationEvent(user, token));
    }

    public void resendRegistrationToken(String expiredToken) throws UserEmailNotFoundException, CommonTokenForSuchUserNotFoundException {
        User user = extractUserFromToken(expiredToken);

        String token = commonTokenService.createNewCommonTokenForUser(user);

        applicationEventPublisher.publishEvent(new OnResendingConfirmationLinkEvent(user, token));
    }

    @Transactional
    public AuthenticationResponse confirmRegistration(
            String token
    ) throws UserEmailNotFoundException, CommonTokenExpiredException, InvalidCommonTokenException, CommonTokenForSuchUserNotFoundException {
        commonTokenService.validateToken(token);

        User user = extractUserFromToken(token);

        user.setEnabled(true);

        commonTokenService.deleteCommonTokenForUser(user);

        return AuthenticationResponse.builder()
                .jwtToken(accessTokenService.generateAccessToken(user))
                .build();
    }

    private User extractUserFromToken(String token) throws UserEmailNotFoundException {
        return commonTokenService.extractUser(token);
    }
}
