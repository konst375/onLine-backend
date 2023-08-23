package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.mappers.CommentMapperImpl;
import com.chirko.onLine.dto.mappers.UserMapperImpl;
import com.chirko.onLine.dto.request.CommentRequestDto;
import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.Comment;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.postgres.CommentRepo;
import com.chirko.onLine.services.CommentService;
import com.chirko.onLine.services.ImgService;
import com.chirko.onLine.services.PostService;
import com.chirko.onLine.services.TagScoresService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CommentService.class,
        CommentMapperImpl.class,
        UserMapperImpl.class
})
class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    @MockBean
    private PostService postService;
    @MockBean
    private ImgService imgService;
    @MockBean
    private TagScoresService tagScoresService;
    @MockBean
    private CommentRepo commentRepo;

    @Test
    void toDto() {
        // given
        User user = User.builder().id(UUID.randomUUID()).name("name").surname("surname").build();

        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .text("some text")
                .user(user)
                .likes(Collections.emptySet())
                .build();

        CommentDto expectedDto = new CommentDto(
                comment.getId().toString(),
                comment.getText(),
                new BaseUserDto(user.getId().toString(), user.getName(), user.getSurname(), null),
                null,
                null,
                0
        );
        // when
        CommentDto actualDto = commentService.toDto(comment);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void addPostComment() {
        // given
        User user = User.builder().id(UUID.randomUUID()).name("name").surname("surname").build();

        Post post = Post.builder().id(UUID.randomUUID()).build();

        CommentRequestDto commentRequestDto = new CommentRequestDto("some text for comment");

        when(postService.getById(post.getId())).thenReturn(post);
        when(commentRepo.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(commentRepo.findUserImages(any(Comment.class))).thenReturn(Optional.of(Collections.emptyList()));

        CommentDto expectedDto = new CommentDto(
                null,
                commentRequestDto.getText(),
                new BaseUserDto(user.getId().toString(), user.getName(), user.getSurname(), null),
                null,
                null,
                0
        );
        // when
        CommentDto actualDto = commentService.addPostComment(post.getId(), user, commentRequestDto);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void ifAddPostCommentAndUserNotFound() {
        // given
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("some text for comment");

        when(commentRepo.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(commentRepo.findUserImages(any(Comment.class))).thenReturn(Optional.empty());
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> commentService.addPostComment(
                UUID.randomUUID(),
                User.builder().build(),
                commentRequestDto));
        assertEquals(ErrorCause.USER_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void addImgComment() {
        // given
        User user = User.builder().id(UUID.randomUUID()).name("name").surname("surname").build();

        Img img = Img.builder().id(UUID.randomUUID()).build();

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("some text for comment");

        when(imgService.getById(img.getId())).thenReturn(img);
        when(commentRepo.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(commentRepo.findUserImages(any(Comment.class))).thenReturn(Optional.of(Collections.emptyList()));

        CommentDto expectedDto = new CommentDto(
                null,
                commentRequestDto.getText(),
                new BaseUserDto(user.getId().toString(), user.getName(), user.getSurname(), null),
                null,
                null,
                0
        );
        // when
        CommentDto actualDto = commentService.addImgComment(img.getId(), user, commentRequestDto);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void ifAddImgCommentAndUserNotFound() {
        // given
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("some text for comment");

        when(commentRepo.findUserImages(any(Comment.class))).thenReturn(Optional.empty());
        when(commentRepo.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class, () -> commentService.addImgComment(
                UUID.randomUUID(),
                User.builder().build(),
                commentRequestDto));
        assertEquals(ErrorCause.USER_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void getPostComments() {
        // given
        Post post = Post.builder().id(UUID.randomUUID()).build();

        User user1 = User.builder().id(UUID.randomUUID()).name("name1").surname("surname1").build();
        User user2 = User.builder().id(UUID.randomUUID()).name("name2").surname("surname2").build();
        User user3 = User.builder().id(UUID.randomUUID()).name("name3").surname("surname3").build();

        Comment comment1 = Comment.builder().id(UUID.randomUUID()).text("1").user(user1).likes(Collections.emptySet()).build();
        Comment comment2 = Comment.builder().id(UUID.randomUUID()).text("2").user(user2).likes(Collections.emptySet()).build();
        Comment comment3 = Comment.builder().id(UUID.randomUUID()).text("3").user(user3).likes(Collections.emptySet()).build();

        when(postService.getById(post.getId())).thenReturn(post);
        when(commentRepo.findAllByPostAndFetchUserImagesEagerly(post))
                .thenReturn(Optional.of(Set.of(comment1, comment2, comment3)));

        Set<CommentDto> expectedCommentDtoSet = Set.of(
                new CommentDto(comment1.getId().toString(),
                        comment1.getText(),
                        new BaseUserDto(user1.getId().toString(), user1.getName(), user1.getSurname(), null),
                        null,
                        null,
                        0),
                new CommentDto(comment2.getId().toString(),
                        comment2.getText(),
                        new BaseUserDto(user2.getId().toString(), user2.getName(), user2.getSurname(), null),
                        null,
                        null,
                        0),
                new CommentDto(comment3.getId().toString(),
                        comment3.getText(),
                        new BaseUserDto(user3.getId().toString(), user3.getName(), user3.getSurname(), null),
                        null,
                        null,
                        0)
        );
        // when
        Set<CommentDto> actualCommentDtoSet = commentService.getPostComments(post.getId());
        // then
        assertEquals(expectedCommentDtoSet, actualCommentDtoSet);
    }

    @Test
    void ifGetPostCommentsAndCommentsNotFound() {
        Set<CommentDto> actualCommentDtoSet = commentService.getPostComments(UUID.randomUUID());
        assertNull(actualCommentDtoSet);
    }

    @Test
    void getImgComments() {
        // given
        Img img = Img.builder().id(UUID.randomUUID()).build();

        User user1 = User.builder().id(UUID.randomUUID()).name("name1").surname("surname1").build();
        User user2 = User.builder().id(UUID.randomUUID()).name("name2").surname("surname2").build();
        User user3 = User.builder().id(UUID.randomUUID()).name("name3").surname("surname3").build();

        Comment comment1 = Comment.builder().id(UUID.randomUUID()).text("1").user(user1).likes(Collections.emptySet()).build();
        Comment comment2 = Comment.builder().id(UUID.randomUUID()).text("2").user(user2).likes(Collections.emptySet()).build();
        Comment comment3 = Comment.builder().id(UUID.randomUUID()).text("3").user(user3).likes(Collections.emptySet()).build();

        when(imgService.getById(img.getId())).thenReturn(img);
        when(commentRepo.findAllByImgWithUserImagesAndLikes(img))
                .thenReturn(Optional.of(Set.of(comment1, comment2, comment3)));

        Set<CommentDto> expectedCommentDtoSet = Set.of(
                new CommentDto(comment1.getId().toString(),
                        comment1.getText(),
                        new BaseUserDto(user1.getId().toString(), user1.getName(), user1.getSurname(), null),
                        null,
                        null,
                        0),
                new CommentDto(comment2.getId().toString(),
                        comment2.getText(),
                        new BaseUserDto(user2.getId().toString(), user2.getName(), user2.getSurname(), null),
                        null,
                        null,
                        0),
                new CommentDto(comment3.getId().toString(),
                        comment3.getText(),
                        new BaseUserDto(user3.getId().toString(), user3.getName(), user3.getSurname(), null),
                        null,
                        null,
                        0)
        );
        // when
        Set<CommentDto> actualCommentDtoSet = commentService.getImgComments(img.getId());
        // then
        assertEquals(expectedCommentDtoSet, actualCommentDtoSet);
    }

    @Test
    void ifGetImgCommentsAndCommentsNotFound() {
        Set<CommentDto> actualCommentDtoSet = commentService.getImgComments(UUID.randomUUID());
        assertNull(actualCommentDtoSet);
    }

    @Test
    void updateComment() {
        // given
        User user = User.builder().id(UUID.randomUUID()).name("name").surname("surname").build();
        Comment comment = Comment.builder().id(UUID.randomUUID()).user(user).likes(Collections.emptySet()).build();

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("some text for comment");

        when(commentRepo.getByIdWithLikes(comment.getId())).thenReturn(Optional.of(comment));

        CommentDto expectedDto = new CommentDto(
                comment.getId().toString(),
                commentRequestDto.getText(),
                new BaseUserDto(user.getId().toString(), user.getName(), user.getSurname(), null),
                null,
                null,
                0);
        // when
        CommentDto actualDto = commentService.updateComment(comment.getId(), user, commentRequestDto);
        // then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void ifUpdateCommentAndAccessDenied() {
        // given
        when(commentRepo.getByIdWithLikes(any(UUID.class))).thenReturn(Optional.of(
                Comment.builder()
                        .user(User.builder().id(UUID.randomUUID()).build())
                        .build()));
        OnLineException thrown = assertThrows(OnLineException.class, () -> commentService.updateComment(
                UUID.randomUUID(),
                User.builder().id(UUID.randomUUID()).build(),
                new CommentRequestDto()));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void ifUpdateCommentAndCommentNotFound() {
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> commentService.updateComment(UUID.randomUUID(), User.builder().build(), new CommentRequestDto()));
        assertEquals(ErrorCause.COMMENT_NOT_FOUND, thrown.getErrorCause());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void deleteComment() {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Comment comment = Comment.builder().id(UUID.randomUUID()).user(user).build();

        when(commentRepo.findById(comment.getId())).thenReturn(Optional.of(comment));
        // when
        commentService.deleteComment(comment.getId(), user);
        // then
        verify(commentRepo, times(1)).delete(comment);
    }

    @Test
    void ifDeleteCommentAndAccessDenied() {
        // given
        User user = User.builder().id(UUID.randomUUID()).build();
        Comment comment = Comment.builder().id(UUID.randomUUID()).user(User.builder().build()).build();

        when(commentRepo.findById(comment.getId())).thenReturn(Optional.of(comment));
        // when
        // then
        OnLineException thrown = assertThrows(OnLineException.class,
                () -> commentService.deleteComment(comment.getId(), user));
        assertEquals(ErrorCause.ACCESS_DENIED, thrown.getErrorCause());
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    void getCommentWithUserImages() {
        // given
        Comment expectedComment = Comment.builder().id(UUID.randomUUID()).user(User.builder().build()).build();
        when(commentRepo.getByIdWithLikes(expectedComment.getId())).thenReturn(Optional.of(expectedComment));
        // when
        Comment actualComment = commentService.getCommentWithUserImagesAndLikes(expectedComment.getId());
        // then
        assertEquals(expectedComment, actualComment);
    }
}