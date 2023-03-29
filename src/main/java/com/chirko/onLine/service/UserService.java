package com.chirko.onLine.service;

import com.chirko.onLine.dto.mapper.UserMapper;
import com.chirko.onLine.dto.request.ResetUserPasswordDto;
import com.chirko.onLine.dto.response.AuthenticationResponseDto;
import com.chirko.onLine.dto.response.UserPageDto;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.event.OnPasswordResetRequestEvent;
import com.chirko.onLine.event.OnSuccessfulPasswordResetEvent;
import com.chirko.onLine.exception.ErrorCause;
import com.chirko.onLine.exception.OnLineException;
import com.chirko.onLine.repo.UserRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AccessTokenService accessTokenService;
    private final CommonTokenService commonTokenService;
    private final ImgService imgService;
    private final PostUtilsService postUtilsService;
    private final UserMapper userMapper;


    public void resetPassword(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() ->
                new OnLineException("User with this email does not exist, email: " + email, ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));

        String token = commonTokenService.createSaveAndGetCommonTokenForUser(user);

        applicationEventPublisher.publishEvent(new OnPasswordResetRequestEvent(user, token));
    }

    @Transactional
    public AuthenticationResponseDto saveResetPassword(@Valid ResetUserPasswordDto resetUserPasswordDto) {
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

    public boolean isOldPasswordValid(String email, String oldPassword) {
        return findUserByEmail(email).getPassword().equals(passwordEncoder.encode(oldPassword));
    }

    @Transactional
    public void updatePassword(String email, String password) {
        findUserByEmail(email).setPassword(passwordEncoder.encode(password));
    }

    public void updateAvatar(MultipartFile avatar, String email) {
        imgService.updateAvatarForUser(avatar, findUserByEmail(email));
    }

    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() ->
                new OnLineException("User with this email does not exist, email: " + email,
                        ErrorCause.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public User findUserAndFetchImagesEagerlyByPost(UUID postId) {
        return userRepo.findUserByPostIdAndFetchAvatarEagerly(postId).orElseThrow(() ->
                new OnLineException("User does not exist", ErrorCause.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public UserPageDto getUserPage(UUID userId) {
        User user = userRepo.findUserByIdAndFetchPostsAndImagesEagerly(userId)
                .orElseThrow(() -> new OnLineException("User not Found, userId: " + userId, ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        user.setPosts(user.getPosts()
                .stream()
                .map(post -> postUtilsService.findPostByIdAndFetchImagesEagerly(post.getId()))
                .collect(Collectors.toSet()));
        return userMapper.userToUserPageDto(user);
    }
}
