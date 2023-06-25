package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.mappers.PostMapperImpl;
import com.chirko.onLine.dto.request.RQPostDto;
import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;
import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.Community;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.Owner;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.PostRepo;
import com.chirko.onLine.services.CommunityService;
import com.chirko.onLine.services.ImgService;
import com.chirko.onLine.services.PostService;
import com.chirko.onLine.services.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        PostService.class,
        PostMapperImpl.class
})
class PostServiceTest {
    @Autowired
    private PostService postService;
    @MockBean
    private TagService tagService;
    @MockBean
    private ImgService imgService;
    @MockBean
    private CommunityService communityService;
    @MockBean
    private PostRepo postRepo;

    @Test
    void ifCreateUserPost() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("name")
                .surname("surname")
                .build();

        RQPostDto rqPostDto = new RQPostDto("Some text", null);

        UserPostDto expectedUserPostDto = new UserPostDto(
                new BaseUserDto(user.getId().toString(), user.getName(), user.getSurname(), null),
                new BasePostDto(null, rqPostDto.getText(), Collections.emptyList(), Collections.emptySet(), null, Owner.USER)
        );

        when(imgService.findUserImages(user)).thenReturn(null);
        when(tagService.createTags(any(String.class))).thenReturn(Collections.emptySet());
        when(postRepo.save(any(Post.class))).then(AdditionalAnswers.returnsFirstArg());
        // when
        UserPostDto actualUserPostDto = postService.createUserPost(user, rqPostDto);
        // then
        assertEquals(expectedUserPostDto, actualUserPostDto);
    }

    @Test
    void ifCreateUserPostAndImagesAtDtoAreNull() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("name")
                .surname("surname")
                .images(Collections.emptyList())
                .build();

        RQPostDto rqPostDto = new RQPostDto("Some text", null);

        UserPostDto expectedDto = new UserPostDto(
                new BaseUserDto(user.getId().toString(), user.getName(), user.getSurname(), null),
                new BasePostDto(null, rqPostDto.getText(), Collections.emptyList(), Collections.emptySet(), null, Owner.USER)
        );

        when(imgService.findUserImages(user)).thenReturn(Collections.emptyList());
        when(tagService.createTags(any(String.class))).thenReturn(Collections.emptySet());
        when(postRepo.save(any(Post.class))).then(AdditionalAnswers.returnsFirstArg());
        // when
        UserPostDto actualDto = postService.createUserPost(user, rqPostDto);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void ifCreateCommunityPost() {
        // given
        Community community = Community.builder()
                .id(UUID.randomUUID())
                .name("name")
                .subject("subject")
                .build();

        RQPostDto rqPostDto = new RQPostDto("Some text", Collections.emptyList());

        CommunityPostDto expectedDto = new CommunityPostDto(
                new BaseCommunityDto(community.getId().toString(), community.getName(), community.getSubject(), null, null),
                new BasePostDto(null, rqPostDto.getText(), Collections.emptyList(), Collections.emptySet(), null, Owner.COMMUNITY)
        );

        when(communityService.getCommunity(community.getId())).thenReturn(community);
        when(tagService.createTags(any(String.class))).thenReturn(Collections.emptySet());
        when(postRepo.save(any(Post.class))).then(AdditionalAnswers.returnsFirstArg());
        // when
        CommunityPostDto actualDto = postService.createCommunityPost(community.getId(), rqPostDto);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void ifGetByIdAndPostNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class, () -> postService.getById(UUID.randomUUID()));
        assertEquals(ErrorCause.POST_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void ifGetPostAndPostNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> postService.getPost(UUID.randomUUID()));
        assertEquals(ErrorCause.POST_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void getPost() {
        // given
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .text("some text")
                .images(Collections.emptyList())
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("name")
                .surname("surname")
                .posts(Set.of(post))
                .build();
        post.setUser(user);
        when(postRepo.findByIdWithTagsAndImages(post.getId())).thenReturn(Optional.of(post));

        BasePostDto expectedDto = new BasePostDto(post.getId().toString(), post.getText(), Collections.emptyList(), null, null, Owner.USER);
        // when
        BasePostDto actualDto = postService.getPost(post.getId());
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void ifJustUserDeletePost() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();

        when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));
        // when
        postService.deletePost(user, post.getId());
        // then
        verify(postRepo, times(1)).delete(post);
    }

    @Test
    void ifJustUserDeletePostAndAccessDenied() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .user(User.builder().build())
                .build();

        when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> postService.deletePost(user, post.getId()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifAdminDeleteCommunityPost() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Community community = Community.builder()
                .id(UUID.randomUUID())
                .admin(user)
                .build();

        user.setCommunities(Set.of(community));

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .community(community)
                .build();

        when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));
        when(communityService.getModerators(post.getCommunity().getId())).thenReturn(Collections.emptySet());
        // when
        postService.deletePost(user, post.getId());
        // then
        verify(postRepo, times(1)).delete(post);
    }

    @Test
    void ifAdminDeleteCommunityPostAndAccessDenied() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Community community = Community.builder()
                .id(UUID.randomUUID())
                .admin(User.builder().build())
                .build();

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .community(community)
                .build();

        when(postRepo.findById(post.getId())).thenReturn(Optional.of(post));
        when(communityService.getModerators(post.getCommunity().getId())).thenReturn(Collections.emptySet());
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> postService.deletePost(user, post.getId()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void toBasePostDto() {
        // given
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .text("some text")
                .modifiedDate(new Timestamp(System.currentTimeMillis()))
                .build();

        BasePostDto expectedDto = new BasePostDto(
                post.getId().toString(),
                post.getText(),
                null,
                null,
                post.getModifiedDate(),
                null);
        // when
        BasePostDto actualDto = postService.toBasePostDto(post);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void toBasePostsDto() {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Post post1 = Post.builder().id(UUID.randomUUID()).user(user).text("1").build();
        Post post2 = Post.builder().id(UUID.randomUUID()).user(user).text("2").build();
        Post post3 = Post.builder().id(UUID.randomUUID()).user(user).text("3").build();
        Set<Post> posts = Set.of(
                post1,
                post2,
                post3);
        when(postRepo.findAllByAdminWithTagsAndImages(any(User.class))).thenReturn(Optional.of(posts));
        Set<BasePostDto> expected = Set.of(
                new BasePostDto(post1.getId().toString(), post1.getText(), null, null, null, Owner.USER),
                new BasePostDto(post2.getId().toString(), post2.getText(), null, null, null, Owner.USER),
                new BasePostDto(post3.getId().toString(), post3.getText(), null, null, null, Owner.USER)
        );
        // when
        Set<BasePostDto> actual = postService.toBasePostsDto(user);
        // then
        assertEquals(expected, actual);
    }

    @Test
    void toBasePostsDtoFoeUserWithoutPosts() {
        // when
        Set<BasePostDto> actual = postService.toBasePostsDto(User.builder().build());
        // then
        assertEquals(Collections.emptySet(), actual);
    }
}