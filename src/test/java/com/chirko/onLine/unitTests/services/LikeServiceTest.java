package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.mappers.*;
import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.Comment;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.Owner;
import com.chirko.onLine.repos.LikeRepo;
import com.chirko.onLine.services.CommentService;
import com.chirko.onLine.services.ImgService;
import com.chirko.onLine.services.LikeService;
import com.chirko.onLine.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        LikeService.class,
        CommentMapperImpl.class,
        UserMapperImpl.class,
        ImgMapperImpl.class,
        PostMapperImpl.class
})
class LikeServiceTest {
    @Autowired
    private LikeService likeService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private PostMapper postMapper;
    @MockBean
    private CommentService commentService;
    @MockBean
    private ImgService imgService;
    @MockBean
    private PostService postService;
    @MockBean
    private LikeRepo likeRepo;

    @Test
    void likeComment() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .text("some comment")
                .user(user)
                .build();

        when(commentService.getCommentWithUserImages(comment.getId())).thenReturn(comment);
        when(commentService.toDto(comment)).thenAnswer(
                invocation -> commentMapper.toDto((Comment) invocation.getArguments()[0])
        );

        CommentDto expectedDto = new CommentDto(
                comment.getText(),
                new BaseUserDto(user.getId().toString(), user.getName(), user.getSurname(), null),
                null,
                null);
        // when
        CommentDto actualDto = likeService.likeComment(comment.getId(), user);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void likeImg() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Img img = Img.builder()
                .id(UUID.randomUUID())
                .build();

        when(imgService.getById(img.getId())).thenReturn(img);
        when(imgService.toDto(img)).thenAnswer(
                invocation -> imgMapper.toDto((Img) invocation.getArguments()[0])
        );

        ImgDto expectedDto = new ImgDto(img.getId().toString(), null, null);
        // when
        ImgDto actualDto = likeService.likeImg(img.getId(), user);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void likePost() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .text("some text")
                .user(user)
                .build();
        when(postService.findPostWithTagsAndImages(post.getId())).thenReturn(post);
        when(postService.toBasePostDto(post)).thenAnswer(
                invocation -> postMapper.toBasePostDto((Post) invocation.getArguments()[0])
        );

        BasePostDto expectedDto = new BasePostDto(
                post.getId().toString(),
                post.getText(),
                null,
                null,
                null,
                Owner.USER);
        // when
        BasePostDto actualDto = likeService.likePost(post.getId(), user);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void unlike() {
        // given
        UUID id = UUID.randomUUID();
        // when
        likeService.unlike(id);
        // then
        verify(likeRepo, times(1)).deleteByParentId(id);
    }
}