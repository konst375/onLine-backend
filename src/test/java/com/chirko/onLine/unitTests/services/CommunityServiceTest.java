package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.mappers.CommunityMapperImpl;
import com.chirko.onLine.dto.mappers.PostMapperImpl;
import com.chirko.onLine.dto.mappers.UserMapperImpl;
import com.chirko.onLine.dto.request.community.RQRegisterCommunityDto;
import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.TagDto;
import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.community.CommunityPageDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.*;
import com.chirko.onLine.entities.enums.Role;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.CommunityRepo;
import com.chirko.onLine.services.CommunityService;
import com.chirko.onLine.services.ImgService;
import com.chirko.onLine.services.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class,})
@ContextConfiguration(classes = {
        CommunityService.class,
        UserMapperImpl.class,
        PostMapperImpl.class,
        CommunityMapperImpl.class
})
class CommunityServiceTest {
    @MockBean
    private ImgService imgService;
    @MockBean
    private TagService tagService;
    @MockBean
    private CommunityRepo communityRepo;
    @Autowired
    private CommunityService communityService;

    @Test
    void ifCreateCommunityWithoutAvatar() {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        String communityTags = "#Test";
        String tagName = "#Test";

        RQRegisterCommunityDto rqRegisterCommunityDto =
                new RQRegisterCommunityDto("testCommunity", "testing", null, communityTags);
        Set<Tag> tags = Set.of(Tag.builder()
                .tagName(tagName)
                .build());
        when(tagService.getCommunityTags(any(Community.class), eq(rqRegisterCommunityDto))).thenReturn(tags);

        Community expectedCommunity = Community.builder()
                .name("testCommunity")
                .subject("testing")
                .admin(user)
                .tags(tags)
                .images(Collections.emptySet())
                .build();
        when(communityRepo.save(any(Community.class))).thenReturn(expectedCommunity);

        BaseCommunityDto expectedCommunityDto =
                new BaseCommunityDto(
                        null,
                        expectedCommunity.getName(),
                        expectedCommunity.getSubject(),
                        null,
                        Set.of(new TagDto(tagName)));
        // when
        BaseCommunityDto actualCommunityDto = communityService.createCommunity(user, rqRegisterCommunityDto);
        // then
        assertEquals(expectedCommunityDto, actualCommunityDto);
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void ifCreateCommunityWithAvatar() throws IOException {
        // given
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        String communityTags = "#Test";
        String tagName = "#Test";

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("defaultAvatar.png", expectedImgBytes);
        RQRegisterCommunityDto rqRegisterCommunityDto = new RQRegisterCommunityDto(
                "testCommunity",
                "testing",
                mockMultipartFile,
                communityTags);
        Set<Tag> tags = Set.of(Tag.builder()
                .tagName(tagName)
                .build());
        when(tagService.getCommunityTags(any(Community.class), eq(rqRegisterCommunityDto))).thenReturn(tags);

        Community expectedCommunity = Community.builder()
                .name("testCommunity")
                .subject("testing")
                .admin(user)
                .tags(tags)
                .build();
        Img avatar = Img.builder()
                .community(expectedCommunity)
                .isAvatar(true)
                .img(expectedImgBytes)
                .build();
        expectedCommunity.setImages(Set.of(avatar));
        when(communityRepo.save(any(Community.class))).thenReturn(expectedCommunity);

        BaseCommunityDto expectedCommunityDto = new BaseCommunityDto(
                null,
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                new ImgDto(null, expectedImgBytes, null),
                Set.of(new TagDto(tagName)));

        when(imgService.buildCommunityAvatar(eq(mockMultipartFile), any(Community.class))).thenReturn(avatar);
        // when
        BaseCommunityDto actualCommunityDto = communityService.createCommunity(user, rqRegisterCommunityDto);
        // then
        assertEquals(expectedCommunityDto.name(), actualCommunityDto.name());
        assertEquals(expectedCommunityDto.subject(), actualCommunityDto.subject());
        assertArrayEquals(expectedCommunityDto.avatar().img(), actualCommunityDto.avatar().img());
        assertEquals(expectedCommunityDto.tags(), actualCommunityDto.tags());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void ifGetCommunityPageAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.getCommunityPage(UUID.randomUUID()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifGetCommunityPageAndCommunitySuccessfullyFound() {
        // given
        Set<Tag> tags = Set.of(
                Tag.builder().id(UUID.randomUUID()).tagName("#Tag").build(),
                Tag.builder().id(UUID.randomUUID()).tagName("#Test").build(),
                Tag.builder().id(UUID.randomUUID()).tagName("#Testing").build());
        Set<TagDto> tagDtoSet = tags.stream()
                .map(tag -> new TagDto(tag.getTagName()))
                .collect(Collectors.toSet());

        Set<Img> images = Set.of(
                Img.builder().id(UUID.randomUUID()).build(),
                Img.builder().id(UUID.randomUUID()).build(),
                Img.builder().id(UUID.randomUUID()).build());
        Set<ImgDto> imgDtoSet = images.stream()
                .map(img -> new ImgDto(img.getId().toString(), null, null))
                .collect(Collectors.toSet());

        UUID communityId = UUID.randomUUID();
        Community expectedCommunity = Community.builder()
                .id(communityId)
                .name("testName")
                .subject("testSubject")
                .tags(tags)
                .images(images)
                .build();
        Set<Post> posts = Set.of(
                Post.builder().id(UUID.randomUUID()).community(expectedCommunity).build(),
                Post.builder().id(UUID.randomUUID()).community(expectedCommunity).build(),
                Post.builder().id(UUID.randomUUID()).community(expectedCommunity).build());
        expectedCommunity.setPosts(posts);
        when(communityRepo.findByIdAndFetchAllDependencies(communityId)).thenReturn(Optional.of(expectedCommunity));

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                null,
                tagDtoSet);
        Set<CommunityPostDto> communityPostDtoSet = posts.stream()
                .map(post -> new CommunityPostDto(
                        baseCommunityDto,
                        new BasePostDto(post.getId().toString(), null, null, null, null)))
                .collect(Collectors.toSet());
        CommunityPageDto expectedCommunityPageDto = new CommunityPageDto(
                baseCommunityDto,
                null,
                imgDtoSet,
                communityPostDtoSet);
        // when
        CommunityPageDto actualCommunityPage = communityService.getCommunityPage(communityId);
        // then
        assertEquals(expectedCommunityPageDto, actualCommunityPage);
    }

    @Test
    void ifGetCommunityAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.getCommunity(UUID.randomUUID()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateAvatarAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.updateAvatar(
                UUID.randomUUID(), null, null));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateAvatarAndAccessDenied() {
        // given
        when(communityRepo.findByIdAndFetchAllDependencies(any(UUID.class))).thenReturn(
                Optional.of(Community.builder().admin(User.builder().id(UUID.randomUUID()).build()).build()));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.updateAvatar(
                UUID.randomUUID(), null, User.builder().id(UUID.randomUUID()).build()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateAvatarThatNotExistsAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder().id(UUID.randomUUID()).admin(user).images(new HashSet<>()).build();
        when(communityRepo.findByIdAndFetchAllDependencies(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("null", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFile)).thenReturn(expectedImgBytes);

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                new ImgDto(null, expectedImgBytes, null),
                null);
        CommunityPageDto expectedCommunityPageDto = new CommunityPageDto(
                baseCommunityDto,
                null,
                Set.of(new ImgDto(null, expectedImgBytes, null)),
                null);
        // when
        CommunityPageDto actualCommunityPageDto = communityService.updateAvatar(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedCommunityPageDto.community().id(), actualCommunityPageDto.community().id());
        assertEquals(expectedCommunityPageDto.community().name(), actualCommunityPageDto.community().name());
        assertEquals(expectedCommunityPageDto.community().subject(), actualCommunityPageDto.community().subject());
        assertArrayEquals(expectedCommunityPageDto.community().avatar().img(), actualCommunityPageDto.community().avatar().img());
        assertEquals(expectedCommunityPageDto.community().tags(), actualCommunityPageDto.community().tags());
        assertEquals(expectedCommunityPageDto.cover(), actualCommunityPageDto.cover());
        assertEquals(1, actualCommunityPageDto.images().size());
        assertEquals(expectedCommunityPageDto.posts(), actualCommunityPageDto.posts());
    }

    @Test
    void ifUpdateAvatarAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder()
                .id(UUID.randomUUID())
                .admin(user)
                .images(Set.of(Img.builder()
                        .isAvatar(true)
                        .img(requireNonNull(getClass().getClassLoader()
                                .getResourceAsStream("static/img.png")).readAllBytes())
                        .build()))
                .build();
        when(communityRepo.findByIdAndFetchAllDependencies(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("null", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFile)).thenReturn(expectedImgBytes);

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                new ImgDto(null, expectedImgBytes, null),
                null);
        CommunityPageDto expectedCommunityPageDto = new CommunityPageDto(
                baseCommunityDto,
                null,
                Set.of(new ImgDto(null, expectedImgBytes, null)),
                null);
        // when
        CommunityPageDto actualCommunityPageDto = communityService.updateAvatar(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedCommunityPageDto.community().id(), actualCommunityPageDto.community().id());
        assertEquals(expectedCommunityPageDto.community().name(), actualCommunityPageDto.community().name());
        assertEquals(expectedCommunityPageDto.community().subject(), actualCommunityPageDto.community().subject());
        assertArrayEquals(expectedCommunityPageDto.community().avatar().img(), actualCommunityPageDto.community().avatar().img());
        assertEquals(expectedCommunityPageDto.community().tags(), actualCommunityPageDto.community().tags());
        assertEquals(expectedCommunityPageDto.cover(), actualCommunityPageDto.cover());
        assertEquals(1, actualCommunityPageDto.images().size());
        assertEquals(expectedCommunityPageDto.posts(), actualCommunityPageDto.posts());
    }

    @Test
    void ifUpdateCoverAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.updateCover(
                UUID.randomUUID(), null, null));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateCoverAndAccessDenied() {
        // given
        when(communityRepo.findByIdAndFetchAllDependencies(any(UUID.class))).thenReturn(
                Optional.of(Community.builder().admin(User.builder().id(UUID.randomUUID()).build()).build()));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.updateCover(
                UUID.randomUUID(), null, User.builder().id(UUID.randomUUID()).build()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateCoverThatNotExistsAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder().id(UUID.randomUUID()).admin(user).images(new HashSet<>()).build();
        when(communityRepo.findByIdAndFetchAllDependencies(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("null", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFile)).thenReturn(expectedImgBytes);

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                null,
                null);
        ImgDto coverDto = new ImgDto(null, expectedImgBytes, null);
        CommunityPageDto expectedCommunityPageDto = new CommunityPageDto(
                baseCommunityDto,
                coverDto,
                Set.of(coverDto),
                null);
        // when
        CommunityPageDto actualCommunityPageDto = communityService.updateCover(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedCommunityPageDto.community().id(), actualCommunityPageDto.community().id());
        assertEquals(expectedCommunityPageDto.community().name(), actualCommunityPageDto.community().name());
        assertEquals(expectedCommunityPageDto.community().subject(), actualCommunityPageDto.community().subject());
        assertEquals(expectedCommunityPageDto.community().avatar(), actualCommunityPageDto.community().avatar());
        assertEquals(expectedCommunityPageDto.community().tags(), actualCommunityPageDto.community().tags());
        assertArrayEquals(expectedCommunityPageDto.cover().img(), actualCommunityPageDto.cover().img());
        assertEquals(1, actualCommunityPageDto.images().size());
        assertEquals(expectedCommunityPageDto.posts(), actualCommunityPageDto.posts());
    }

    @Test
    void ifUpdateCoverAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder()
                .id(UUID.randomUUID())
                .admin(user)
                .images(Set.of(Img.builder()
                        .isCover(true)
                        .img(requireNonNull(getClass().getClassLoader()
                                .getResourceAsStream("static/img.png")).readAllBytes())
                        .build()))
                .build();
        when(communityRepo.findByIdAndFetchAllDependencies(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("null", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFile)).thenReturn(expectedImgBytes);

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                null,
                null);
        ImgDto coverDto = new ImgDto(null, expectedImgBytes, null);
        CommunityPageDto expectedCommunityPageDto = new CommunityPageDto(
                baseCommunityDto,
                coverDto,
                Set.of(coverDto),
                null);
        // when
        CommunityPageDto actualCommunityPageDto = communityService.updateCover(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedCommunityPageDto.community().id(), actualCommunityPageDto.community().id());
        assertEquals(expectedCommunityPageDto.community().name(), actualCommunityPageDto.community().name());
        assertEquals(expectedCommunityPageDto.community().subject(), actualCommunityPageDto.community().subject());
        assertEquals(expectedCommunityPageDto.community().avatar(), actualCommunityPageDto.community().avatar());
        assertEquals(expectedCommunityPageDto.community().tags(), actualCommunityPageDto.community().tags());
        assertArrayEquals(expectedCommunityPageDto.cover().img(), actualCommunityPageDto.cover().img());
        assertEquals(1, actualCommunityPageDto.images().size());
        assertEquals(expectedCommunityPageDto.posts(), actualCommunityPageDto.posts());
    }

    @Test
    void ifDeleteCommunityAndCommunityNotFoundById() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.deleteCommunity(UUID.randomUUID(), User.builder().build()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifDeleteCommunityAndCommunityNotFoundByAdmin() {
        // given
        when(communityRepo.findById(any(UUID.class))).thenReturn(Optional.of(Community.builder().build()));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.deleteCommunity(UUID.randomUUID(), User.builder().build()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifDeleteCommunityAndAccessDenied() {
        // given
        when(communityRepo.findById(any(UUID.class))).thenReturn(Optional.of(Community.builder().build()));
        when(communityRepo.findByAdmin(any(User.class))).thenReturn(Optional.of(Collections.emptySet()));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.deleteCommunity(UUID.randomUUID(), User.builder().build()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifDeleteCommunityAndAccessGranted() {
        // given
        Community community = Community.builder().id(UUID.randomUUID()).build();
        when(communityRepo.findById(any(UUID.class))).thenReturn(Optional.of(community));
        when(communityRepo.findByAdmin(any(User.class))).thenReturn(Optional.of(Set.of(community)));
        // when
        communityService.deleteCommunity(community.getId(), User.builder().build());
        // then
        verify(communityRepo, times(1)).delete(community);
    }

    @Test
    void ifSubscribeAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.subscribe(
                UUID.randomUUID(), null));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifSubscribe() {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community community = Community.builder()
                .id(UUID.randomUUID())
                .followers(new HashSet<>())
                .images(Collections.emptySet())
                .build();
        when(communityRepo.findByIdAndFetchAllDependencies(community.getId())).thenReturn(Optional.of(community));

        CommunityPageDto expectedCommunityPageDto = new CommunityPageDto(
                new BaseCommunityDto(community.getId().toString(), null, null, null, null),
                null,
                Collections.emptySet(),
                null);
        // when
        CommunityPageDto actualCommunityPageDto = communityService.subscribe(community.getId(), user);
        // then
        assertEquals(expectedCommunityPageDto, actualCommunityPageDto);
        assertEquals(1, community.getFollowers().size());
    }

    @Test
    void ifUnsubscribeAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.unsubscribe(
                UUID.randomUUID(), null));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifUnsubscribe() {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community community = Community.builder()
                .id(UUID.randomUUID())
                .followers(new HashSet<>())
                .images(Collections.emptySet())
                .build();
        community.getFollowers().add(user);
        when(communityRepo.findByIdAndFetchAllDependencies(community.getId())).thenReturn(Optional.of(community));

        CommunityPageDto expectedCommunityPageDto = new CommunityPageDto(
                new BaseCommunityDto(community.getId().toString(), null, null, null, null),
                null,
                Collections.emptySet(),
                null);
        // when
        CommunityPageDto actualCommunityPageDto = communityService.unsubscribe(community.getId(), user);
        // then
        assertEquals(expectedCommunityPageDto, actualCommunityPageDto);
        assertEquals(0, community.getFollowers().size());
    }

    @Test
    void ifGetFollowersAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.getFollowers(UUID.randomUUID()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifGetFollowers() {
        // given
        Set<User> userSet = Set.of(
                User.builder().id(UUID.randomUUID()).images(Collections.emptySet()).build(),
                User.builder().id(UUID.randomUUID()).images(Collections.emptySet()).build(),
                User.builder().id(UUID.randomUUID()).images(Collections.emptySet()).build());
        Community community = Community.builder().id(UUID.randomUUID()).followers(userSet).build();

        when(communityRepo.findFollowersByCommunityId(community.getId())).thenReturn(Optional.of(userSet));

        when(imgService.findUserImages(any(User.class))).thenReturn(Collections.emptySet());

        Set<BaseUserDto> expectedFollowerDtoSet = userSet.stream()
                .map(user -> new BaseUserDto(user.getId().toString(), null, null, null))
                .collect(Collectors.toSet());
        // when
        Set<BaseUserDto> actualFollowerDtoSet = communityService.getFollowers(community.getId());
        // then
        assertEquals(expectedFollowerDtoSet, actualFollowerDtoSet);
    }
}