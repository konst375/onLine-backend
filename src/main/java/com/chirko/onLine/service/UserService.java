package com.chirko.onLine.service;

import com.chirko.onLine.dto.request.ResetUserPasswordDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.event.OnPasswordResetRequestEvent;
import com.chirko.onLine.event.OnSuccessfulPasswordResetEvent;
import com.chirko.onLine.exception.PostNotFoundException;
import com.chirko.onLine.exception.UserEmailNotFoundException;
import com.chirko.onLine.repo.UserRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccessTokenService accessTokenService;
    private final CommonTokenService commonTokenService;
    private final ImgService imgService;

    public void resetPassword(String email) throws UserEmailNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(UserEmailNotFoundException::new);

        String token = commonTokenService.createSaveAndGetCommonTokenForUser(user);

        applicationEventPublisher.publishEvent(new OnPasswordResetRequestEvent(user, token));
    }

    @Transactional
    public AuthenticationResponseDto saveResetPassword(
            @Valid ResetUserPasswordDto resetUserPasswordDto
    ) throws Exception {

        String token = resetUserPasswordDto.getToken();
        commonTokenService.validateToken(token);

        User user = commonTokenService.extractUser(token);

        user.setPassword(passwordEncoder.encode(resetUserPasswordDto.getPassword()));

        applicationEventPublisher.publishEvent(new OnSuccessfulPasswordResetEvent(user));

        commonTokenService.deleteCommonTokenForUser(user);

        return AuthenticationResponseDto.builder()
                .jwtToken(accessTokenService.generateAccessToken(user))
                .build();
    }

    public boolean isOldPasswordValid(String email, String oldPassword) throws UserEmailNotFoundException {
        return findUserByEmail(email).getPassword().equals(passwordEncoder.encode(oldPassword));
    }

    @Transactional
    public void updatePassword(String email, String password) throws UserEmailNotFoundException {
        findUserByEmail(email).setPassword(passwordEncoder.encode(password));
    }

    public void updateAvatar(MultipartFile avatar, String email) throws UserEmailNotFoundException {
        imgService.updateAvatarForUser(avatar, findUserByEmail(email));
    }

    public User findUserByEmail(String email) throws UserEmailNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(UserEmailNotFoundException::new);
    }

    public User findUserAndFetchImagedEagerlyByPost(UUID postId) throws PostNotFoundException {
        return userRepo.findUserAndFetchAvatarEagerlyByPostId(postId).orElseThrow(PostNotFoundException::new);
    }
}
