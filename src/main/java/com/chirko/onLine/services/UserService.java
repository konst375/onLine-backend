package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.PostMapper;
import com.chirko.onLine.dto.mappers.UserMapper;
import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final ImgService imgService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final UserRepo userRepo;

    @Transactional
    public UserPageDto updateAvatar(UUID userId, MultipartFile avatar) {
        User user = userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)
                .orElseThrow(() -> new OnLineException("User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        byte[] img;
        if (avatar != null) {
            img = imgService.getBytes(avatar);
            if (user.getAvatar() != null) {
                user.getAvatar().setImg(img);
            } else {
                user.getImages().add(Img.builder()
                        .img(img)
                        .isAvatar(true)
                        .user(user)
                        .build());
            }
        }
        Set<UserPostDto> posts = postMapper.toUserPostsDto(user.getPosts());
        return userMapper.userToUserPageDto(user, posts);
    }

    @Transactional
    public UserPageDto updateCover(UUID userId, MultipartFile cover) {
        User user = userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)
                .orElseThrow(() -> new OnLineException("User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        byte[] img;
        if (cover != null) {
            img = imgService.getBytes(cover);
            if (user.getCover() != null) {
                user.getCover().setImg(img);
            } else {
                user.getImages().add(Img.builder()
                        .img(img)
                        .isCover(true)
                        .user(user)
                        .build());
            }
        }
        Set<UserPostDto> posts = postMapper.toUserPostsDto(user.getPosts());
        return userMapper.userToUserPageDto(user, posts);
    }

    public UserPageDto getUserPage(UUID userId) {
        User user = userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId).orElseThrow(() -> new OnLineException(
                "User not Found, userId: " + userId, ErrorCause.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        Set<UserPostDto> posts = postMapper.toUserPostsDto(user.getPosts());
        return userMapper.userToUserPageDto(user, posts);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }
}
