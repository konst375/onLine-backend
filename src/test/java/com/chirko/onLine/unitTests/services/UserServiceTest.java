package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.mappers.PostMapperImpl;
import com.chirko.onLine.dto.mappers.UserMapperImpl;
import com.chirko.onLine.dto.response.user.UserPageDto;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.postgres.UserRepo;
import com.chirko.onLine.services.ImgService;
import com.chirko.onLine.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserService.class,
        UserMapperImpl.class,
        PostMapperImpl.class
})
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private ImgService imgService;
//    @MockBean
//    private PostService postService;
    @MockBean
    private UserRepo userRepo;

    @Test
    void ifUserNotFoundWhenTryingToUpdateAvatar() {
        // given
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", (byte[]) null);
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> userService.updateAvatar(UUID.randomUUID(), mockMultipartFile));
        assertEquals(ErrorCause.USER_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
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
                .images(new ArrayList<>())
                .posts(Collections.emptySet())
                .build();
        when(userRepo.findByIdWithImages(userId)).thenReturn(Optional.of(expectedUser));
        // expected avatar creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass()
                .getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")
        ).readAllBytes();
        MockMultipartFile mockMultipartFileForNewAvatar = new MockMultipartFile(
                "defaultAvatar.png",
                expectedImgBytes);
        Img expectedAvatar = Img.builder()
                .img(expectedImgBytes)
                .isAvatar(true)
                .build();
        when(imgService.createAvatar(mockMultipartFileForNewAvatar)).thenReturn(expectedAvatar);
        // mocked userRepo set up
        when(userRepo.save(expectedUser)).thenReturn(expectedUser);
        // when
        UserPageDto actualDto = userService.updateAvatar(userId, mockMultipartFileForNewAvatar);
        // then
        assertEquals(expectedUser.getName(), actualDto.name());
        assertEquals(expectedUser.getSurname(), actualDto.surname());
        assertEquals(expectedUser.getBirthday(), actualDto.birthday());
        assertArrayEquals(expectedImgBytes, actualDto.avatar().img());
        assertEquals(1, actualDto.images().size());
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
                .images(new ArrayList<>())
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
        when(userRepo.findByIdWithImages(userId)).thenReturn(Optional.of(expectedUser));
        when(userRepo.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        // expected avatar creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/img.png")).readAllBytes();
        MockMultipartFile mockMultipartFileForNewAvatar = new MockMultipartFile("img.png", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFileForNewAvatar)).thenReturn(expectedImgBytes);
        // when
        UserPageDto actualDto = userService.updateAvatar(userId, mockMultipartFileForNewAvatar);
        // then
        assertEquals(expectedUser.getName(), actualDto.name());
        assertEquals(expectedUser.getSurname(), actualDto.surname());
        assertEquals(expectedUser.getBirthday(), actualDto.birthday());
        assertArrayEquals(expectedImgBytes, actualDto.avatar().img());
        assertEquals(1, actualDto.images().size());
    }

    @Test
    void ifUserNotFoundWhenTryingToUpdateCover() {
        // given
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", (byte[]) null);
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> userService.updateCover(UUID.randomUUID(), mockMultipartFile));
        assertEquals(ErrorCause.USER_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
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
                .images(new ArrayList<>())
                .posts(Collections.emptySet())
                .build();
        when(userRepo.findByIdWithImages(userId)).thenReturn(Optional.of(expectedUser));
        // expected cover creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass()
                .getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")
        ).readAllBytes();
        MockMultipartFile mockMultipartFileForNewCover = new MockMultipartFile(
                "defaultAvatar.png",
                expectedImgBytes);
        Img expectedCover = Img.builder()
                .img(expectedImgBytes)
                .isCover(true)
                .build();
        when(imgService.createCover(mockMultipartFileForNewCover)).thenReturn(expectedCover);
        // mocked userRepo set up
        when(userRepo.save(expectedUser)).thenReturn(expectedUser);
        // when
        UserPageDto actualDto = userService.updateCover(userId, mockMultipartFileForNewCover);
        // then
        assertEquals(expectedUser.getName(), actualDto.name());
        assertEquals(expectedUser.getSurname(), actualDto.surname());
        assertEquals(expectedUser.getBirthday(), actualDto.birthday());
        assertArrayEquals(expectedImgBytes, actualDto.cover().img());
        assertEquals(1, actualDto.images().size());
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
                .images(new ArrayList<>())
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
        when(userRepo.findByIdWithImages(userId)).thenReturn(Optional.of(expectedUser));
        when(userRepo.save(any(User.class))).then(AdditionalAnswers.returnsFirstArg());
        // expected cover creates and mocked imgService set up
        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/img.png")).readAllBytes();
        MockMultipartFile mockMultipartFileForNewCover = new MockMultipartFile("img.png", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFileForNewCover)).thenReturn(expectedImgBytes);
        // when
        UserPageDto actualDto = userService.updateCover(userId, mockMultipartFileForNewCover);
        // then
        assertEquals(expectedUser.getName(), actualDto.name());
        assertEquals(expectedUser.getSurname(), actualDto.surname());
        assertEquals(expectedUser.getBirthday(), actualDto.birthday());
        assertArrayEquals(expectedImgBytes, actualDto.cover().img());
        assertEquals(1, actualDto.images().size());
    }

    @Test
    void ifUserNotFoundWhenTryingToGetUserPage() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> userService.getUserPage(UUID.randomUUID()));
        assertEquals(ErrorCause.USER_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
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
                .images(Collections.emptyList())
                .posts(Collections.emptySet())
                .build();
        when(userRepo.findByIdWithImages(userId)).thenReturn(Optional.of(expectedUser));
        // then
        UserPageDto actualDto = userService.getUserPage(userId);
        // when
        assertEquals(userId.toString(), actualDto.id());
        assertEquals(expectedUser.getName(), actualDto.name());
        assertEquals(expectedUser.getSurname(), actualDto.surname());
        assertEquals(expectedUser.getBirthday(), actualDto.birthday());
        assertEquals(0, actualDto.images().size());
        assertEquals(0, actualDto.posts().size());
    }

    @Test
    void deleteUser() {
        // when
        userService.deleteUser(User.builder().build());
        // then
        verify(userRepo, times(1)).delete(any(User.class));
    }

    @Test
    void updateUserRoleToAdmin() {
        //given
        User user = User.builder().id(UUID.randomUUID()).build();
        // when
        userService.giveAdmin(user);
        // then
        verify(userRepo, times(1)).save(user);
    }
}