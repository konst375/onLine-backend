package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.PostMapper;
import com.chirko.onLine.dto.mappers.UserMapper;
import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private ImgService imgService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PostMapper postMapper;

    // userService.updateAvatar() method tests
    @Test
    void ifUserNotFoundWhenTryingToUpdateAvatar() {
        // given
        UUID userId = UUID.randomUUID();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", (byte[]) null);
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenThrow(
                new OnLineException("User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        // when
        // then
        Assertions.assertThrows(OnLineException.class,
                () -> userService.updateAvatar(userId, mockMultipartFile),
                "User not found, userId: " + userId);
    }

    @Test
    void ifLogicOfUpdatingTheAvatarDoesNotYetExistsIsCorrect() throws IOException {
        // given
        // expected user creates and mocked userRepo set up
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder()
                .id(userId)
                .name("name")
                .surname("surname")
                .enabled(true)
                .birthday(LocalDate.of(2000, 11, 4))
                .images(new HashSet<>())
                .posts(Collections.emptySet())
                .build();
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenReturn(Optional.of(expectedUser));
        // expected avatar creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFileForNewAvatar = new MockMultipartFile("defaultAvatar.png", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFileForNewAvatar)).thenReturn(expectedImgBytes);
        // mocked postMapper set up
        when(postMapper.toUserPostsDto(Collections.emptySet())).thenReturn(Collections.emptySet());
        // mocked userMapper set up
        ImgDto avatarDto = new ImgDto(null, expectedImgBytes, null);
        when(userMapper.userToUserPageDto(expectedUser, Collections.emptySet())).thenReturn(new UserPageDto(
                expectedUser.getId().toString(),
                expectedUser.getName(),
                expectedUser.getSurname(),
                avatarDto,
                null,
                expectedUser.getBirthday(),
                Set.of(avatarDto),
                Collections.emptySet()));
        // when
        UserPageDto actualUserPageDto = userService.updateAvatar(userId, mockMultipartFileForNewAvatar);
        // then
        assertEquals(expectedUser.getName(), actualUserPageDto.name());
        assertEquals(expectedUser.getSurname(), actualUserPageDto.surname());
        assertEquals(expectedUser.getBirthday(), actualUserPageDto.birthday());
        assertArrayEquals(expectedImgBytes, actualUserPageDto.avatar().img());
        assertEquals(1, actualUserPageDto.images().size());
    }

    @Test
    void ifLogicOfUpdatingExistingAvatarIsCorrect() throws IOException {
        // given
        // expected user with avatar creates and mocked userRepo set up
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder()
                .id(userId)
                .name("name")
                .surname("surname")
                .enabled(true)
                .birthday(LocalDate.of(2000, 11, 4))
                .images(new HashSet<>())
                .posts(Collections.emptySet())
                .build();
        byte[] oldUserAvatarBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        Img oldAvatar = Img.builder()
                .isAvatar(true)
                .user(expectedUser)
                .img(oldUserAvatarBytes)
                .build();
        expectedUser.getImages().add(oldAvatar);
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenReturn(Optional.of(expectedUser));
        // expected avatar creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/img.png")).readAllBytes();
        MockMultipartFile mockMultipartFileForNewAvatar = new MockMultipartFile("img.png", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFileForNewAvatar)).thenReturn(expectedImgBytes);
        // mocked postMapper set up
        when(postMapper.toUserPostsDto(Collections.emptySet())).thenReturn(Collections.emptySet());
        // mocked userMapper set up
        ImgDto avatarDto = new ImgDto(null, expectedImgBytes, null);
        when(userMapper.userToUserPageDto(expectedUser, Collections.emptySet())).thenReturn(new UserPageDto(
                expectedUser.getId().toString(),
                expectedUser.getName(),
                expectedUser.getSurname(),
                avatarDto,
                null,
                expectedUser.getBirthday(),
                Set.of(avatarDto),
                Collections.emptySet()));
        // when
        UserPageDto actualUserPageDto = userService.updateAvatar(userId, mockMultipartFileForNewAvatar);
        // then
        assertEquals(expectedUser.getName(), actualUserPageDto.name());
        assertEquals(expectedUser.getSurname(), actualUserPageDto.surname());
        assertEquals(expectedUser.getBirthday(), actualUserPageDto.birthday());
        assertArrayEquals(expectedImgBytes, actualUserPageDto.avatar().img());
        assertEquals(1, actualUserPageDto.images().size());
    }

    // userService.updateCover() tests
    @Test
    void ifUserNotFoundWhenTryingToUpdateCover() {
        // given
        UUID userId = UUID.randomUUID();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", (byte[]) null);
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenThrow(
                new OnLineException("User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        // when
        // then
        Assertions.assertThrows(OnLineException.class,
                () -> userService.updateCover(userId, mockMultipartFile),
                "User not found, userId: " + userId);
    }

    @Test
    void ifLogicOfUpdatingTheCoverDoesNotYetExistsIsCorrect() throws IOException {
        // given
        // expected user creates and mocked userRepo set up
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder()
                .id(userId)
                .name("name")
                .surname("surname")
                .enabled(true)
                .birthday(LocalDate.of(2000, 11, 4))
                .images(new HashSet<>())
                .posts(Collections.emptySet())
                .build();
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenReturn(Optional.of(expectedUser));
        // expected cover creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFileForNewCover = new MockMultipartFile("defaultAvatar.png", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFileForNewCover)).thenReturn(expectedImgBytes);
        // mocked postMapper set up
        when(postMapper.toUserPostsDto(Collections.emptySet())).thenReturn(Collections.emptySet());
        // mocked userMapper set up
        ImgDto coverDto = new ImgDto(null, expectedImgBytes, null);
        when(userMapper.userToUserPageDto(expectedUser, Collections.emptySet())).thenReturn(new UserPageDto(
                expectedUser.getId().toString(),
                expectedUser.getName(),
                expectedUser.getSurname(),
                null,
                coverDto,
                expectedUser.getBirthday(),
                Set.of(coverDto),
                Collections.emptySet()));
        // when
        UserPageDto actualUserPageDto = userService.updateCover(userId, mockMultipartFileForNewCover);
        // then
        assertEquals(expectedUser.getName(), actualUserPageDto.name());
        assertEquals(expectedUser.getSurname(), actualUserPageDto.surname());
        assertEquals(expectedUser.getBirthday(), actualUserPageDto.birthday());
        assertArrayEquals(expectedImgBytes, actualUserPageDto.cover().img());
        assertEquals(1, actualUserPageDto.images().size());
    }

    @Test
    void ifLogicOfUpdatingExistingCoverIsCorrect() throws IOException {
        // given
        // expected user with cover creates and mocked userRepo set up
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder()
                .id(userId)
                .name("name")
                .surname("surname")
                .enabled(true)
                .birthday(LocalDate.of(2000, 11, 4))
                .images(new HashSet<>())
                .posts(Collections.emptySet())
                .build();
        byte[] oldUserCoverBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        Img oldCover = Img.builder()
                .isCover(true)
                .user(expectedUser)
                .img(oldUserCoverBytes)
                .build();
        expectedUser.getImages().add(oldCover);
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenReturn(Optional.of(expectedUser));
        // expected cover creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/img.png")).readAllBytes();
        MockMultipartFile mockMultipartFileForNewCover = new MockMultipartFile("img.png", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFileForNewCover)).thenReturn(expectedImgBytes);
        // mocked postMapper set up
        when(postMapper.toUserPostsDto(Collections.emptySet())).thenReturn(Collections.emptySet());
        // mocked userMapper set up
        ImgDto coverDto = new ImgDto(null, expectedImgBytes, null);
        when(userMapper.userToUserPageDto(expectedUser, Collections.emptySet())).thenReturn(new UserPageDto(
                expectedUser.getId().toString(),
                expectedUser.getName(),
                expectedUser.getSurname(),
                null,
                coverDto,
                expectedUser.getBirthday(),
                Set.of(coverDto),
                Collections.emptySet()));
        // when
        UserPageDto actualUserPageDto = userService.updateCover(userId, mockMultipartFileForNewCover);
        // then
        assertEquals(expectedUser.getName(), actualUserPageDto.name());
        assertEquals(expectedUser.getSurname(), actualUserPageDto.surname());
        assertEquals(expectedUser.getBirthday(), actualUserPageDto.birthday());
        assertArrayEquals(expectedImgBytes, actualUserPageDto.cover().img());
        assertEquals(1, actualUserPageDto.images().size());
    }

    // userService.getUserPage() method tests
    @Test
    void ifUserNotFoundWhenTryingToGetUserPage() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenThrow(
                new OnLineException("User not found, userId: " + userId,
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        // when
        // then
        Assertions.assertThrows(OnLineException.class,
                () -> userService.getUserPage(userId), "User not found, userId: " + userId);
    }

    @Test
    void ifExistedUserTryToGetUserPage() {
        // given
        // expected user creates and mocked userRepo set up
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder()
                .id(userId)
                .name("name")
                .surname("surname")
                .enabled(true)
                .birthday(LocalDate.of(2000, 11, 4))
                .images(Collections.emptySet())
                .posts(Collections.emptySet())
                .build();
        when(userRepo.findByIdAndFetchUserImagesAndPostsEagerly(userId)).thenReturn(Optional.of(expectedUser));
        // mocked postMapper set up
        when(postMapper.toUserPostsDto(Collections.emptySet())).thenReturn(Collections.emptySet());
        // mocked userMapper set up
        when(userMapper.userToUserPageDto(expectedUser, Collections.emptySet())).thenReturn(new UserPageDto(
                expectedUser.getId().toString(),
                expectedUser.getName(),
                expectedUser.getSurname(),
                null,
                null,
                expectedUser.getBirthday(),
                Collections.emptySet(),
                Collections.emptySet()));
        // then
        UserPageDto actualUserPageDto = userService.getUserPage(userId);
        // when
        assertEquals(userId.toString(), actualUserPageDto.id());
        assertEquals(expectedUser.getName(), actualUserPageDto.name());
        assertEquals(expectedUser.getSurname(), actualUserPageDto.surname());
        assertEquals(expectedUser.getBirthday(), actualUserPageDto.birthday());
        assertEquals(0, actualUserPageDto.images().size());
        assertEquals(0, actualUserPageDto.posts().size());
    }
}