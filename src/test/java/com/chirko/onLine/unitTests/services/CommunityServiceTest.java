package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.mappers.CommunityMapperImpl;
import com.chirko.onLine.dto.mappers.PostMapperImpl;
import com.chirko.onLine.dto.mappers.UserMapperImpl;
import com.chirko.onLine.dto.request.community.RQRegisterCommunityDto;
import com.chirko.onLine.dto.response.TagDto;
import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.community.CommunityPageDto;
import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.*;
import com.chirko.onLine.entities.enums.Owner;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.CommunityRepo;
import com.chirko.onLine.services.CommunityService;
import com.chirko.onLine.services.ImgService;
import com.chirko.onLine.services.TagService;
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
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        when(tagService.createTags(any(String.class))).thenReturn(tags);

        Community expectedCommunity = Community.builder()
                .name("testCommunity")
                .subject("testing")
                .admin(user)
                .tags(tags)
                .images(Collections.emptyList())
                .build();
        when(communityRepo.save(any(Community.class))).then(AdditionalAnswers.returnsFirstArg());

        BaseCommunityDto expectedDto =
                new BaseCommunityDto(
                        null,
                        expectedCommunity.getName(),
                        expectedCommunity.getSubject(),
                        null,
                        Set.of(new TagDto(tagName)));
        // when
        BaseCommunityDto actualDto = communityService.createCommunity(user, rqRegisterCommunityDto);
        // then
        assertEquals(expectedDto, actualDto);
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
        when(tagService.createTags(any(String.class))).thenReturn(tags);

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
        expectedCommunity.setImages(List.of(avatar));
        when(communityRepo.save(any(Community.class))).then(AdditionalAnswers.returnsFirstArg());

        BaseCommunityDto expectedDto = new BaseCommunityDto(
                null,
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                new BaseImgDto(null, expectedImgBytes, null, null),
                Set.of(new TagDto(tagName)));

        when(imgService.createAvatar(eq(mockMultipartFile))).thenReturn(avatar);
        // when
        BaseCommunityDto actualDto = communityService.createCommunity(user, rqRegisterCommunityDto);
        // then
        assertEquals(expectedDto.name(), actualDto.name());
        assertEquals(expectedDto.subject(), actualDto.subject());
        assertArrayEquals(expectedDto.avatar().img(), actualDto.avatar().img());
        assertEquals(expectedDto.tags(), actualDto.tags());
    }

    @Test
    void ifGetCommunityPageAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.getCommunityPage(UUID.randomUUID()));
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

        List<Img> images = List.of(
                Img.builder().id(UUID.randomUUID()).build(),
                Img.builder().id(UUID.randomUUID()).build(),
                Img.builder().id(UUID.randomUUID()).build());
        List<BaseImgDto> baseImgDtoSet = images.stream()
                .map(img -> new BaseImgDto(img.getId().toString(), null, null, null))
                .collect(Collectors.toList());

        UUID communityId = UUID.randomUUID();
        Community expectedCommunity = Community.builder()
                .id(communityId)
                .name("testName")
                .subject("testSubject")
                .tags(tags)
                .images(images)
                .posts(Collections.emptySet())
                .build();
        Set<Post> posts = Set.of(
                Post.builder().id(UUID.randomUUID()).community(expectedCommunity).images(Collections.emptyList())
                        .likes(Collections.emptySet()).comments(Collections.emptySet()).build(),
                Post.builder().id(UUID.randomUUID()).community(expectedCommunity).images(Collections.emptyList())
                        .likes(Collections.emptySet()).comments(Collections.emptySet()).build(),
                Post.builder().id(UUID.randomUUID()).community(expectedCommunity).images(Collections.emptyList())
                        .likes(Collections.emptySet()).comments(Collections.emptySet()).build());
        expectedCommunity.setPosts(posts);
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(communityId))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.findCommunityWithPostsAndTheirImagesAndTags(communityId))
                .thenReturn(Optional.of(expectedCommunity));

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                null,
                tagDtoSet);
        Set<BasePostDto> basePostsDto = posts.stream()
                .map(post -> new BasePostDto(
                        post.getId().toString(),
                        null,
                        Collections.emptyList(),
                        null,
                        null,
                        Owner.COMMUNITY,
                        0,
                        Collections.emptySet(),
                        0))
                .collect(Collectors.toSet());
        CommunityPageDto expectedDto = new CommunityPageDto(
                baseCommunityDto,
                null,
                baseImgDtoSet,
                basePostsDto);
        // when
        CommunityPageDto actualDto = communityService.getCommunityPage(communityId);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void ifGetCommunityAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.getCommunity(UUID.randomUUID()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void getCommunity() {
        // given
        Community expected = Community.builder().id(UUID.randomUUID()).images(Collections.emptyList()).build();
        when(communityRepo.findByIdWithTagsAndImages(expected.getId())).thenReturn(Optional.of(expected));
        // when
        Community actual = communityService.getCommunity(expected.getId());
        // then
        assertEquals(expected, actual);
    }

    @Test
    void ifUpdateAvatarAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.updateAvatar(UUID.randomUUID(), new MockMultipartFile("name", (byte[]) null), null));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateAvatarAndAccessDenied() {
        // given
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(any(UUID.class))).thenReturn(
                Optional.of(Community.builder().admin(User.builder().id(UUID.randomUUID()).build()).build()));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.updateAvatar(
                UUID.randomUUID(), new MockMultipartFile("name", (byte[]) null), User.builder().id(UUID.randomUUID()).build()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateAvatarThatNotExistsAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder()
                .id(UUID.randomUUID())
                .admin(user)
                .images(new ArrayList<>())
                .posts(Collections.emptySet())
                .build();
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.findCommunityWithPostsAndTheirImagesAndTags(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.save(any(Community.class))).then(AdditionalAnswers.returnsFirstArg());

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("null", expectedImgBytes);
        when(imgService.createAvatar(mockMultipartFile))
                .thenReturn(Img.builder().img(expectedImgBytes).isAvatar(true).build());

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                new BaseImgDto(null, expectedImgBytes, null, null),
                null);
        CommunityPageDto expectedDto = new CommunityPageDto(
                baseCommunityDto,
                null,
                List.of(new BaseImgDto(null, expectedImgBytes, null, null)),
                Collections.emptySet());
        // when
        CommunityPageDto actualDto = communityService.updateAvatar(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedDto.community().id(), actualDto.community().id());
        assertEquals(expectedDto.community().name(), actualDto.community().name());
        assertEquals(expectedDto.community().subject(), actualDto.community().subject());
        assertArrayEquals(expectedDto.community().avatar().img(), actualDto.community().avatar().img());
        assertEquals(expectedDto.community().tags(), actualDto.community().tags());
        assertEquals(expectedDto.cover(), actualDto.cover());
        assertEquals(1, actualDto.images().size());
        assertEquals(expectedDto.posts(), actualDto.posts());
    }

    @Test
    void ifUpdateAvatarAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder()
                .id(UUID.randomUUID())
                .admin(user)
                .images(List.of(Img.builder()
                        .isAvatar(true)
                        .img(requireNonNull(getClass().getClassLoader().getResourceAsStream("static/img.png")).readAllBytes())
                        .build()))
                .posts(Collections.emptySet())
                .build();
        when(communityRepo.findCommunityWithPostsAndTheirImagesAndTags(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));

        when(communityRepo.save(any(Community.class))).then(AdditionalAnswers.returnsFirstArg());

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader().getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("null", expectedImgBytes);
        when(imgService.getBytes(mockMultipartFile)).thenReturn(expectedImgBytes);

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                new BaseImgDto(null, expectedImgBytes, null, null),
                null);
        CommunityPageDto expectedDto = new CommunityPageDto(
                baseCommunityDto,
                null,
                List.of(new BaseImgDto(null, expectedImgBytes, null, null)),
                Collections.emptySet());
        // when
        CommunityPageDto actualDto = communityService.updateAvatar(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedDto.community().id(), actualDto.community().id());
        assertEquals(expectedDto.community().name(), actualDto.community().name());
        assertEquals(expectedDto.community().subject(), actualDto.community().subject());
        assertArrayEquals(expectedDto.community().avatar().img(), actualDto.community().avatar().img());
        assertEquals(expectedDto.community().tags(), actualDto.community().tags());
        assertEquals(expectedDto.cover(), actualDto.cover());
        assertEquals(1, actualDto.images().size());
        assertEquals(expectedDto.posts(), actualDto.posts());
    }

    @Test
    void ifUpdateCoverAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.updateCover(UUID.randomUUID(), new MockMultipartFile("name", (byte[]) null), null));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateCoverAndAccessDenied() {
        // given
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(any(UUID.class))).thenReturn(
                Optional.of(Community.builder().admin(User.builder().id(UUID.randomUUID()).build()).build()));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> communityService.updateCover(
                UUID.randomUUID(),
                new MockMultipartFile("name", (byte[]) null),
                User.builder().id(UUID.randomUUID()).build()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateCoverThatNotExistsAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder()
                .id(UUID.randomUUID())
                .admin(user)
                .images(Collections.emptyList())
                .posts(Collections.emptySet())
                .build();

        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.findCommunityWithPostsAndTheirImagesAndTags(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.save(any(Community.class))).then(AdditionalAnswers.returnsFirstArg());

        byte[] expectedImgBytes = requireNonNull(getClass().getClassLoader()
                .getResourceAsStream("static/defaultAvatar.png")).readAllBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("name", expectedImgBytes);
        when(imgService.createCover(mockMultipartFile)).thenReturn(Img.builder().img(expectedImgBytes).isCover(true).build());

        BaseCommunityDto baseCommunityDto = new BaseCommunityDto(
                expectedCommunity.getId().toString(),
                expectedCommunity.getName(),
                expectedCommunity.getSubject(),
                null,
                null);
        BaseImgDto coverDto = new BaseImgDto(null, expectedImgBytes, null, null);
        CommunityPageDto expectedDto = new CommunityPageDto(
                baseCommunityDto,
                coverDto,
                List.of(coverDto),
                Collections.emptySet());
        // when
        CommunityPageDto actualDto = communityService.updateCover(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedDto.community().id(), actualDto.community().id());
        assertEquals(expectedDto.community().name(), actualDto.community().name());
        assertEquals(expectedDto.community().subject(), actualDto.community().subject());
        assertEquals(expectedDto.community().avatar(), actualDto.community().avatar());
        assertEquals(expectedDto.community().tags(), actualDto.community().tags());
        assertArrayEquals(expectedDto.cover().img(), actualDto.cover().img());
        assertEquals(1, actualDto.images().size());
        assertEquals(expectedDto.posts(), actualDto.posts());
    }

    @Test
    void ifUpdateCoverAndAccessGranted() throws IOException {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community expectedCommunity = Community.builder()
                .id(UUID.randomUUID())
                .admin(user)
                .images(List.of(Img.builder()
                        .isCover(true)
                        .img(requireNonNull(getClass().getClassLoader()
                                .getResourceAsStream("static/img.png")).readAllBytes())
                        .build()))
                .posts(Collections.emptySet())
                .build();
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.findCommunityWithPostsAndTheirImagesAndTags(expectedCommunity.getId()))
                .thenReturn(Optional.of(expectedCommunity));
        when(communityRepo.save(any(Community.class))).then(AdditionalAnswers.returnsFirstArg());

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
        BaseImgDto coverDto = new BaseImgDto(null, expectedImgBytes, null, null);
        CommunityPageDto expectedDto = new CommunityPageDto(
                baseCommunityDto,
                coverDto,
                List.of(coverDto),
                Collections.emptySet());
        // when
        CommunityPageDto actualDto = communityService.updateCover(expectedCommunity.getId(), mockMultipartFile, user);
        // then
        assertEquals(expectedDto.community().id(), actualDto.community().id());
        assertEquals(expectedDto.community().name(), actualDto.community().name());
        assertEquals(expectedDto.community().subject(), actualDto.community().subject());
        assertEquals(expectedDto.community().avatar(), actualDto.community().avatar());
        assertEquals(expectedDto.community().tags(), actualDto.community().tags());
        assertArrayEquals(expectedDto.cover().img(), actualDto.cover().img());
        assertEquals(1, actualDto.images().size());
        assertEquals(expectedDto.posts(), actualDto.posts());
    }

    @Test
    void ifDeleteCommunityAndCommunityNotFoundById() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.deleteCommunity(UUID.randomUUID(), User.builder().build()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifDeleteCommunityAndAccessDenied() {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        when(communityRepo.findById(any(UUID.class))).thenReturn(Optional.of(Community.builder().admin(user).build()));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.deleteCommunity(UUID.randomUUID(), User.builder().id(UUID.randomUUID()).build()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifDeleteCommunityAndAccessGranted() {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Community community = Community.builder().id(UUID.randomUUID()).admin(user).build();
        when(communityRepo.findById(any(UUID.class))).thenReturn(Optional.of(community));
        // when
        communityService.deleteCommunity(community.getId(), user);
        // then
        verify(communityRepo, times(1)).delete(community);
    }

    @Test
    void ifSubscribeAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.subscribe(UUID.randomUUID(), null));
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
                .images(Collections.emptyList())
                .build();
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(community.getId())).thenReturn(Optional.of(community));

        CommunityPageDto expectedDto = new CommunityPageDto(
                new BaseCommunityDto(community.getId().toString(), null, null, null, null),
                null,
                Collections.emptyList(),
                null);
        // when
        CommunityPageDto actualDto = communityService.subscribe(community.getId(), user);
        // then
        assertEquals(expectedDto, actualDto);
        assertEquals(1, community.getFollowers().size());
    }

    @Test
    void ifUnsubscribeAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.unsubscribe(UUID.randomUUID(), null));
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
                .images(Collections.emptyList())
                .build();
        community.getFollowers().add(user);
        when(communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(community.getId())).thenReturn(Optional.of(community));

        CommunityPageDto expectedDto = new CommunityPageDto(
                new BaseCommunityDto(community.getId().toString(), null, null, null, null),
                null,
                Collections.emptyList(),
                null);
        // when
        CommunityPageDto actualDto = communityService.unsubscribe(community.getId(), user);
        // then
        assertEquals(expectedDto, actualDto);
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
                User.builder().id(UUID.randomUUID()).images(Collections.emptyList()).build(),
                User.builder().id(UUID.randomUUID()).images(Collections.emptyList()).build(),
                User.builder().id(UUID.randomUUID()).images(Collections.emptyList()).build());
        Community community = Community.builder().id(UUID.randomUUID()).followers(userSet).build();

        when(communityRepo.findFollowersByCommunityId(community.getId())).thenReturn(Optional.of(userSet));

        when(imgService.findUserImages(any(User.class))).thenReturn(Collections.emptyList());

        Set<BaseUserDto> expectedDtoSet = userSet.stream()
                .map(user -> new BaseUserDto(user.getId().toString(), null, null, null))
                .collect(Collectors.toSet());
        // when
        Set<BaseUserDto> actualDtoSet = communityService.getFollowers(community.getId());
        // then
        assertEquals(expectedDtoSet, actualDtoSet);
    }

    @Test
    void ifGetModeratorsAndCommunityNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> communityService.getModerators(UUID.randomUUID()));
        assertEquals(ErrorCause.COMMUNITY_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifGetModerators() {
        // given
        UUID communityId = UUID.randomUUID();
        Set<User> expectedModerators = Collections.emptySet();
        when(communityRepo.findModeratorsById(communityId)).thenReturn(Optional.of(expectedModerators));
        // when
        Set<User> actualModerators = communityService.getModerators(communityId);
        // then
        assertEquals(expectedModerators, actualModerators);
    }
}