package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.CommentMapper;
import com.chirko.onLine.dto.request.RQCommentDto;
import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.entities.Comment;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.CommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentService {
    private final PostService postService;
    private final ImgService imgService;
    private final CommentMapper commentMapper;
    private final CommentRepo commentRepo;

    public CommentDto toDto(Comment comment) {
        return commentMapper.toDto(comment);
    }

    public CommentDto addPostComment(UUID postId, User user, RQCommentDto dto) {
        Comment comment = buildComment(user, dto);
        comment.setPost(postService.getById(postId));
        Comment savedComment = commentRepo.save(comment);
        savedComment.getUser().setImages(commentRepo.findUserImages(savedComment)
                .orElseThrow(() -> new OnLineException(
                        "User not found, userId: " + user.getId(),
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND)));
        return commentMapper.toDto(savedComment);
    }

    public CommentDto addImgComment(UUID imgId, User user, RQCommentDto dto) {
        Comment comment = buildComment(user, dto);
        comment.setImg(imgService.getById(imgId));
        Comment savedComment = commentRepo.save(comment);
        savedComment.getUser().setImages(commentRepo.findUserImages(savedComment)
                .orElseThrow(() -> new OnLineException(
                        "User not found, userId: " + user.getId(),
                        ErrorCause.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND)));
        return commentMapper.toDto(savedComment);
    }

    public Set<CommentDto> getPostComments(UUID postId) {
        Post post = postService.getById(postId);
        Set<Comment> comments = commentRepo.findAllByPostAndFetchUserImagesEagerly(post).orElse(null);
        return commentMapper.commentsToCommentsDto(comments);
    }

    public Set<CommentDto> getImgComments(UUID imgId) {
        Img img = imgService.getById(imgId);
        Set<Comment> comments = commentRepo.findAllByImgAndFetchUserImagesEagerly(img).orElse(null);
        return commentMapper.commentsToCommentsDto(comments);
    }

    @Transactional
    public CommentDto updateComment(UUID commentId, User user, RQCommentDto dto) {
        Comment comment = getById(commentId);
        checkUserAccess(comment, user);
        comment.setText(dto.getText());
        return commentMapper.toDto(comment);
    }

    public void deleteComment(UUID commentId, User user) {
        Comment comment = getById(commentId);
        checkUserAccess(comment, user);
        commentRepo.delete(comment);
    }

    public Comment getCommentWithUserImages(UUID commentId) {
        Comment comment = getById(commentId);
        comment.getUser().setImages(commentRepo.findUserImages(comment).orElse(null));
        return comment;
    }

    private void checkUserAccess(Comment comment, User user) {
        if (!comment.getUser().equals(user)) {
            throw new OnLineException(
                    "Comment updating permission denied, userId: " + user.getId(),
                    ErrorCause.ACCESS_DENIED,
                    HttpStatus.FORBIDDEN);
        }
    }

    private Comment getById(UUID commentId) {
        return commentRepo.findById(commentId).orElseThrow(() -> new OnLineException(
                "Comment not found, commentId: " +
                commentId, ErrorCause.COMMENT_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    private Comment buildComment(User user, RQCommentDto dto) {
        return Comment.builder()
                .user(user)
                .text(dto.getText())
                .build();
    }
}
