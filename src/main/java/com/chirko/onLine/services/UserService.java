package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.PostMapper;
import com.chirko.onLine.dto.mappers.UserMapper;
import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.UserRepo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
    public UserPageDto updateAvatar(UUID userId, @NonNull MultipartFile avatar) {
        User user = userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)
                .orElseThrow(() -> new OnLineException(
                        "User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        user.setImages(Lists.newArrayList(Sets.newLinkedHashSet(user.getImages())));
        if (user.getAvatar() != null) {
            user.getAvatar().setImg(imgService.getBytes(avatar));
        } else {
            user.getImages().add(imgService.createAvatar(avatar));
        }
        Set<UserPostDto> posts = postMapper.toUserPostsDto(user.getPosts());
        return userMapper.userToUserPageDto(user, posts);// if avatar not yet exist will return dto without id and create date
    }

    @Transactional
    public UserPageDto updateCover(UUID userId, @NonNull MultipartFile cover) {
        User user = userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)
                .orElseThrow(() -> new OnLineException(
                        "User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        if (user.getCover() != null) {
            user.getCover().setImg(imgService.getBytes(cover));
        } else {
            user.getImages().add(imgService.createCover(cover));
        }
        Set<UserPostDto> posts = postMapper.toUserPostsDto(user.getPosts());
        return userMapper.userToUserPageDto(user, posts);// if cover not yet exist will return dto without id and create date
    }

    public UserPageDto getUserPage(UUID userId) {
        User user = userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId).orElseThrow(() -> new OnLineException(
                "User not Found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        Set<UserPostDto> posts = postMapper.toUserPostsDto(user.getPosts());
        return userMapper.userToUserPageDto(user, posts);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }
}
