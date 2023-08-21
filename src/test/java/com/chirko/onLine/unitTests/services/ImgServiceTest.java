package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.mappers.CommentMapperImpl;
import com.chirko.onLine.dto.mappers.ImgMapperImpl;
import com.chirko.onLine.dto.mappers.UserMapperImpl;
import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.Like;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.postgres.ImgRepo;
import com.chirko.onLine.services.ImgService;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ImgService.class,
        ImgMapperImpl.class,
        CommentMapperImpl.class,
        UserMapperImpl.class
})
class ImgServiceTest {
    @Autowired
    private ImgService imgService;
    @MockBean
    private ImgRepo imgRepo;

    @Test
    void getById() {
        // given
        UUID imgId = UUID.randomUUID();
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> imgService.getById(imgId));
        assertEquals(ErrorCause.IMAGE_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
        verify(imgRepo, times(1)).findById(imgId);
    }

    @Test
    void getBytes() throws IOException {
        // given
        byte[] expectedBytes = getExpectedBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", expectedBytes);
        // when
        byte[] actualBytes = imgService.getBytes(mockMultipartFile);
        // then
        assertArrayEquals(expectedBytes, actualBytes);
    }

    @Test
    void ifFindUserImagesAndImgNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> imgService.findUserImages(User.builder().build()));
        assertEquals(ErrorCause.IMAGE_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void createImages() throws IOException {
        // given
        byte[] expectedBytes1 = getExpectedBytes();
        byte[] expectedBytes2 = requireNonNull(getClass()
                .getClassLoader()
                .getResourceAsStream("static/img.png")
        ).readAllBytes();
        List<byte[]> expectedBytesSet = List.of(expectedBytes1, expectedBytes2);
        List<MultipartFile> fileSet = List.of(
                new MockMultipartFile("1", expectedBytes1),
                new MockMultipartFile("2", expectedBytes2));
        // when
        List<Img> actualImages = imgService.createImages(fileSet);
        // then
        List<byte[]> actualBytesSet = actualImages.stream().map(Img::getImg).toList();
        assertEquals(expectedBytesSet, actualBytesSet);
    }

    @Test
    void ifCreateImagesWithNullMultipartFileSet() {
        assertNull(imgService.createImages(null));
    }

    @Test
    void createAvatar() throws IOException {
        // given
        byte[] expectedBytes = getExpectedBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", expectedBytes);
        // when
        Img actualImg = imgService.createAvatar(mockMultipartFile);
        // then
        assertNull(actualImg.getId());
        assertArrayEquals(expectedBytes, actualImg.getImg());
        assertTrue(actualImg.isAvatar());
    }

    @Test
    void ifCreateAvatarWithNullMultipartFile() {
        assertNull(imgService.createAvatar(null));
    }

    @Test
    void createCover() throws IOException {
        // given
        byte[] expectedBytes = getExpectedBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", expectedBytes);
        // when
        Img actualImg = imgService.createCover(mockMultipartFile);
        // then
        assertNull(actualImg.getId());
        assertArrayEquals(expectedBytes, actualImg.getImg());
        assertTrue(actualImg.isCover());
    }

    @Test
    void toDto() {
        // given
        Img img = Img.builder()
                .id(UUID.randomUUID())
                .build();
        BaseImgDto expectedDto = new BaseImgDto(img.getId().toString(), null, null, null);
        // when
        BaseImgDto actualDto = imgService.toDto(img);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getFullImgById() {
        Img expected = Img.builder()
                .id(UUID.randomUUID())
                .likes(Sets.newHashSet(Like.builder().id(UUID.randomUUID()).user(User.builder().build()).build()))
                .build();
        when(imgRepo.findByIdWithLikesAndComments(expected.getId())).thenReturn(Optional.of(expected));
        when(imgRepo.findImagesByUser(any())).thenReturn(Optional.of(Collections.emptyList()));
        Img actual = imgService.getFullImgById(expected.getId());
        assertEquals(expected, actual);
    }

    private byte[] getExpectedBytes() throws IOException {
        return requireNonNull(getClass()
                .getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")
        ).readAllBytes();
    }
}