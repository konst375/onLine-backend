package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.UserMapper;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.Role;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.UserRepo;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final ImgService imgService;
    private final PostService postService;
    private final UserMapper userMapper;
    private final UserRepo userRepo;

    public UserPageDto updateAvatar(UUID userId, @NonNull MultipartFile avatar) {
        User user = userRepo.findUserByIdAndFetchImagesEagerly(userId)
                .orElseThrow(() -> new OnLineException(
                        "User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        if (user.getAvatar() != null) {
            user.getAvatar().setImg(imgService.getBytes(avatar));
        } else {
            Img img = imgService.createAvatar(avatar);
            img.setUser(user);
            user.getImages().add(img);
        }
        User savedUser = userRepo.save(user);
        Set<BasePostDto> posts = postService.toBasePostsDto(savedUser);
        return userMapper.userToUserPageDto(savedUser, posts);
    }

    public UserPageDto updateCover(UUID userId, @NonNull MultipartFile cover) {
        User user = userRepo.findUserByIdAndFetchImagesEagerly(userId)
                .orElseThrow(() -> new OnLineException(
                        "User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        if (user.getCover() != null) {
            user.getCover().setImg(imgService.getBytes(cover));
        } else {
            Img img = imgService.createCover(cover);
            img.setUser(user);
            user.getImages().add(img);
        }
        User savedUser = userRepo.save(user);
        Set<BasePostDto> posts = postService.toBasePostsDto(savedUser);
        return userMapper.userToUserPageDto(savedUser, posts);
    }

    public UserPageDto getUserPage(UUID userId) {
        User user = userRepo.findUserByIdAndFetchImagesEagerly(userId).orElseThrow(() -> new OnLineException(
                "User not Found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        Set<BasePostDto> posts = postService.toBasePostsDto(user);
        return userMapper.userToUserPageDto(user, posts);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }

    public void updateRoleToAdmin(User user) {
        user.setRole(Role.ADMIN);
        userRepo.save(user);
    }
}
