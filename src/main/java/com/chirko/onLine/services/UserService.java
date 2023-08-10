package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.UserMapper;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.Role;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.UserRepo;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final ActivityService activityService;
    private final ImgService imgService;
    private final PostService postService;
    private final UserMapper userMapper;
    private final UserRepo userRepo;

    public UserPageDto updateAvatar(UUID userId, @NonNull MultipartFile avatar) {
        User user = userRepo.findByIdWithImages(userId)
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
        Set<BasePostDto> posts = postService.getBasePostsDtoForUser(savedUser);
        return userMapper.userToUserPageDto(savedUser, posts);
    }

    public UserPageDto updateCover(UUID userId, @NonNull MultipartFile cover) {
        User user = userRepo.findByIdWithImages(userId)
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
        Set<BasePostDto> posts = postService.getBasePostsDtoForUser(savedUser);
        return userMapper.userToUserPageDto(savedUser, posts);
    }

    public UserPageDto getUserPage(UUID userId) {
        User user = userRepo.findByIdWithImages(userId).orElseThrow(() -> new OnLineException(
                "User not Found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        Set<BasePostDto> posts = postService.getBasePostsDtoForUser(user);
        return userMapper.userToUserPageDto(user, posts);
    }

    public void deleteUser(User user) {
        userRepo.delete(user);
    }

    public void updateRoleToAdmin(User user) {
        user.setRole(Role.ADMIN);
        userRepo.save(user);
    }

    public User findByIdWithInterestIndicators(UUID userId) {
        return userRepo.findByIdWithInterestIndicators(userId).orElseThrow(() -> new OnLineException(
                "User not found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void markPostsViewed(UUID userId, Set<UUID> viewedPostsIds) {
        User user = userRepo.findUserWithViewedPosts(userId).orElseThrow(() -> new OnLineException(
                "User not found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        Set<Post> newViewedPosts = Sets.newHashSet(postService.findPostsWithAllDependencies(viewedPostsIds));
        newViewedPosts.forEach(post -> post.getViewers().add(user));
        user.getViewedPosts().addAll(newViewedPosts);
    }

    public void logTheActiveDay(User user) {
        activityService.logTheActiveDay(user);
    }

    public User findByIdWithImages(UUID userId) {
        return userRepo.findByIdWithImages(userId).orElseThrow(() -> new OnLineException(
                "User not found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    public User findById(UUID userId) {
        return userRepo.findById(userId).orElseThrow(() -> new OnLineException(
                "User not found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    public Set<User> findAllByIdWithImages(Set<UUID> ids) {
        return Sets.newHashSet(userRepo.findAllByIdWithImages(ids).orElseThrow(() -> new OnLineException(
                "Users not found",
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND)));
    }

    public  Set<User> findAllById(Set<UUID> ids) {
        return Sets.newHashSet(userRepo.findAllById(ids));
    }

    public User findByIdWithChats(UUID userId) {
        return userRepo.findByIdWithChats(userId).orElseThrow(() -> new OnLineException(
                "User not found, userId: " + userId,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    public Set<BaseUserDto> toBaseUsersDto(Set<User> users) {
        return userMapper.toBaseUsersDto(users);
    }

    public List<String> getTimezones() {
        return userRepo.findTimezones().orElseThrow(() -> new OnLineException(
                "User's timezones not found, maybe cause there are no registered users",
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    public List<User> findAllByTimezone(String zone) {
        return userRepo.findAllByTimezone(zone).orElseThrow(() -> new OnLineException(
                "Users by timezone not found, timezone: " + zone,
                ErrorCause.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }
}
